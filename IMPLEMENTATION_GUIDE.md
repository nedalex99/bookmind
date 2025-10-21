# BookMind2 - AI Book Recommendations Implementation Guide

## Overview

This guide documents the implementation of AI-powered book recommendations, manual book additions, and user reviews in the BookMind2 app, following the existing MVI architecture pattern.

## What Has Been Implemented

### ✅ Completed Features

1. **Data Models** (`data/models/`)
   - `Book.kt` - Book entity with all fields (title, author, genres, ratings, etc.)
   - `Review.kt` - User review model with ratings and text
   - `UserBook.kt` - User's library with reading status (Want to Read, Currently Reading, Finished)
   - `UserPreference.kt` - User preferences for genres and authors
   - `BookRecommendation.kt` - AI recommendation model with scoring

2. **Domain Layer** (`domain/`)
   - `BookRepository.kt` - Interface for book operations
   - `ReviewRepository.kt` - Interface for review operations
   - `UserPreferenceRepository.kt` - Interface for user preferences

3. **Data Layer** (`data/`)
   - `BookRepositoryImpl.kt` - Supabase implementation with AI recommendations
   - `ReviewRepositoryImpl.kt` - Supabase reviews implementation
   - `UserPreferenceRepositoryImpl.kt` - Supabase preferences implementation

4. **Routes** (`app/Route.kt`)
   - Added MainGraph, BookList, BookDetail, AddBook, Recommendations, MyLibrary

5. **BookList Feature** (Complete MVI)
   - `BookListVS.kt`, `BookListAction.kt`, `BookListResult.kt`, `BookListNavigation.kt`
   - `BookListRR.kt` (Reducer)
   - `BookListVM.kt` (ViewModel)
   - `BookListScreen.kt` (UI with search, book cards, empty state)

6. **AddBook Feature** (Complete MVI)
   - `AddBookModels.kt` (VS, Action, Result, Navigation)
   - `AddBookRR.kt` (Reducer)
   - `AddBookVM.kt` (ViewModel with validation)

7. **Configuration**
   - Updated `AppModule.kt` with all DI bindings
   - Updated `SupabaseProvider.kt` to include Postgrest
   - Added `supabase-postgrest` dependency to `build.gradle.kts`

## AI Recommendation Algorithm

The implemented algorithm in `BookRepositoryImpl.getRecommendations()` works as follows:

1. **Get User's Reading History**
   - Fetch all books the user has read/is reading

2. **Extract Preferences**
   - Aggregate favorite genres from read books
   - Identify favorite authors

3. **Generate Recommendations**
   - Find books matching favorite genres (3 top genres, 3 books each)
   - Find more books by favorite authors (2 top authors, 2 books each)
   - Calculate relevance score (0.0 - 1.0) based on:
     - Genre match: 30% weight
     - Author match: 40% weight
     - Average rating: 30% weight

4. **Fallback Strategy**
   - If no reading history exists, recommend highly-rated books (4.0+ stars)

5. **Deduplication & Ranking**
   - Remove duplicate recommendations
   - Sort by score (highest first)
   - Return top 10 recommendations

## Database Schema (Supabase)

You need to create these tables in your Supabase project:

### 1. Books Table

```sql
CREATE TABLE books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT,
    cover_image_url TEXT,
    description TEXT,
    genres TEXT[] DEFAULT '{}',
    published_year INTEGER,
    page_count INTEGER,
    average_rating DECIMAL(3,2) DEFAULT 0.0,
    ratings_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    added_by_user_id UUID REFERENCES auth.users(id)
);

-- Indexes for better performance
CREATE INDEX idx_books_title ON books USING GIN(to_tsvector('english', title));
CREATE INDEX idx_books_author ON books USING GIN(to_tsvector('english', author));
CREATE INDEX idx_books_genres ON books USING GIN(genres);
CREATE INDEX idx_books_rating ON books(average_rating DESC);

-- Enable Row Level Security
ALTER TABLE books ENABLE ROW LEVEL SECURITY;

-- Policy: Anyone can read books
CREATE POLICY "Books are viewable by everyone"
ON books FOR SELECT
USING (true);

-- Policy: Authenticated users can insert books
CREATE POLICY "Authenticated users can insert books"
ON books FOR INSERT
WITH CHECK (auth.role() = 'authenticated');

-- Policy: Users can update their own books
CREATE POLICY "Users can update their own books"
ON books FOR UPDATE
USING (auth.uid() = added_by_user_id);

-- Policy: Users can delete their own books
CREATE POLICY "Users can delete their own books"
ON books FOR DELETE
USING (auth.uid() = added_by_user_id);
```

### 2. Reviews Table

```sql
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    user_name TEXT,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(book_id, user_id)
);

-- Index for fetching reviews by book
CREATE INDEX idx_reviews_book ON reviews(book_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);

-- Enable RLS
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;

-- Policy: Anyone can read reviews
CREATE POLICY "Reviews are viewable by everyone"
ON reviews FOR SELECT
USING (true);

-- Policy: Authenticated users can insert reviews
CREATE POLICY "Authenticated users can insert reviews"
ON reviews FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Policy: Users can update their own reviews
CREATE POLICY "Users can update their own reviews"
ON reviews FOR UPDATE
USING (auth.uid() = user_id);

-- Policy: Users can delete their own reviews
CREATE POLICY "Users can delete their own reviews"
ON reviews FOR DELETE
USING (auth.uid() = user_id);

-- Trigger to update book ratings when review is added/updated/deleted
CREATE OR REPLACE FUNCTION update_book_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE books
    SET
        average_rating = (SELECT AVG(rating) FROM reviews WHERE book_id = COALESCE(NEW.book_id, OLD.book_id)),
        ratings_count = (SELECT COUNT(*) FROM reviews WHERE book_id = COALESCE(NEW.book_id, OLD.book_id))
    WHERE id = COALESCE(NEW.book_id, OLD.book_id);
    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER review_rating_trigger
AFTER INSERT OR UPDATE OR DELETE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_book_rating();
```

### 3. User Books Table (Library)

```sql
CREATE TABLE user_books (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    book_id UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    status TEXT NOT NULL CHECK (status IN ('want_to_read', 'currently_reading', 'finished')),
    started_reading_at TIMESTAMP WITH TIME ZONE,
    finished_reading_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, book_id)
);

-- Indexes
CREATE INDEX idx_user_books_user ON user_books(user_id);
CREATE INDEX idx_user_books_status ON user_books(user_id, status);

-- Enable RLS
ALTER TABLE user_books ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only see their own library
CREATE POLICY "Users can view their own library"
ON user_books FOR SELECT
USING (auth.uid() = user_id);

-- Policy: Users can add books to their library
CREATE POLICY "Users can add to their library"
ON user_books FOR INSERT
WITH CHECK (auth.uid() = user_id);

-- Policy: Users can update their library
CREATE POLICY "Users can update their library"
ON user_books FOR UPDATE
USING (auth.uid() = user_id);

-- Policy: Users can remove from their library
CREATE POLICY "Users can delete from their library"
ON user_books FOR DELETE
USING (auth.uid() = user_id);
```

### 4. User Preferences Table

```sql
CREATE TABLE user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES auth.users(id) ON DELETE CASCADE,
    favorite_genres TEXT[] DEFAULT '{}',
    favorite_authors TEXT[] DEFAULT '{}',
    disliked_genres TEXT[] DEFAULT '{}',
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE user_preferences ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only access their own preferences
CREATE POLICY "Users can view their own preferences"
ON user_preferences FOR SELECT
USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own preferences"
ON user_preferences FOR INSERT
WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own preferences"
ON user_preferences FOR UPDATE
USING (auth.uid() = user_id);
```

## Remaining Features to Implement

### 1. BookDetail Screen (with Reviews)

Create the following files:

```
presentation/features/books/detail/blocks/
├── model/
│   ├── BookDetailVS.kt
│   ├── BookDetailAction.kt
│   ├── BookDetailResult.kt
│   └── BookDetailNavigation.kt
├── BookDetailRR.kt
├── BookDetailVM.kt
└── compose/
    └── BookDetailScreen.kt
```

**BookDetailVS** should include:
- `book: Book?`
- `reviews: List<Review>`
- `userReview: Review?`
- `isInLibrary: Boolean`
- `libraryStatus: ReadingStatus?`
- `isLoading: Boolean`
- `error: String?`

**Key Actions**:
- `LoadBookDetails`
- `AddToLibrary(status: ReadingStatus)`
- `RemoveFromLibrary`
- `AddReview(rating: Int, text: String)`
- `UpdateReview(rating: Int, text: String)`
- `DeleteReview`

### 2. Recommendations Screen

Create similar structure to BookList but:
- Call `bookRepository.getRecommendations(userId)` instead of `getAllBooks()`
- Display recommendation reason for each book
- Show score as a match percentage

### 3. MyLibrary Screen

Similar to BookList but with tabs:
- Want to Read
- Currently Reading
- Finished

Filter books using `getUserBooks(userId, status)`

### 4. AddBook Screen (UI)

Create `AddBookScreen.kt` with:
- Text fields for title, author, ISBN, description
- Genre multi-select chips
- Cover image URL field
- Save/Cancel buttons
- Form validation errors display

### 5. Sample Data Seeding

Add some initial books to test the app:

```sql
INSERT INTO books (title, author, genres, description, cover_image_url, published_year, average_rating, ratings_count)
VALUES
('The Midnight Library', 'Matt Haig', ARRAY['Fiction', 'Fantasy'], 'Between life and death there is a library...', 'https://example.com/cover1.jpg', 2020, 4.2, 150),
('Project Hail Mary', 'Andy Weir', ARRAY['Science Fiction', 'Adventure'], 'A lone astronaut must save the earth...', 'https://example.com/cover2.jpg', 2021, 4.5, 200),
('Atomic Habits', 'James Clear', ARRAY['Self-Help', 'Psychology'], 'Tiny changes, remarkable results...', 'https://example.com/cover3.jpg', 2018, 4.7, 300);
```

## Integration with App.kt

Update `App.kt` to add the new navigation routes:

```kotlin
@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.EnrollmentGraph
        ) {
            // Auth flow
            navigation<Route.EnrollmentGraph>(startDestination = Route.SignIn) {
                composable<Route.SignIn> {
                    val vm = koinViewModel<LoginVM>()
                    LoginScreen(vm, navController)
                }
                composable<Route.SignUp> {
                    val vm = koinViewModel<SignUpVM>()
                    SignUpScreen(vm, navController)
                }
            }

            // Main app flow
            navigation<Route.MainGraph>(startDestination = Route.BookList) {
                composable<Route.BookList> {
                    val vm = koinViewModel<BookListVM>()
                    BookListScreen(vm, navController)
                }

                composable<Route.BookDetail> { backStackEntry ->
                    val bookDetail: Route.BookDetail = backStackEntry.toRoute()
                    val vm = koinViewModel<BookDetailVM>()
                    // Pass bookId to VM
                    BookDetailScreen(vm, navController)
                }

                composable<Route.AddBook> {
                    val vm = koinViewModel<AddBookVM>()
                    AddBookScreen(vm, navController)
                }

                composable<Route.Recommendations> {
                    val vm = koinViewModel<RecommendationsVM>()
                    RecommendationsScreen(vm, navController)
                }

                composable<Route.MyLibrary> {
                    val vm = koinViewModel<MyLibraryVM>()
                    MyLibraryScreen(vm, navController)
                }
            }
        }
    }
}
```

## Testing

1. **Unit Tests** - Test the recommendation algorithm:
```kotlin
@Test
fun `recommendation algorithm prioritizes genre and author matches`() {
    // Given user has read sci-fi books by Andy Weir
    // When getting recommendations
    // Then other Andy Weir books and sci-fi books should score highest
}
```

2. **Integration Tests** - Test repository implementations with mock Supabase client

3. **UI Tests** - Test user flows like adding books, writing reviews

## Future Enhancements

1. **Machine Learning Integration**
   - Integrate with actual AI service (OpenAI, Gemini) for better recommendations
   - Use collaborative filtering based on similar users

2. **Book API Integration**
   - Integrate with Google Books API or Open Library API
   - Auto-fill book details from ISBN

3. **Social Features**
   - Follow other users
   - See friends' reading lists
   - Book clubs

4. **Analytics**
   - Reading statistics dashboard
   - Reading goals and streaks

5. **Offline Support**
   - Use Room for local caching
   - Sync when online

## File Structure Summary

```
composeApp/src/commonMain/kotlin/com/nedalex/bookmind/
├── data/
│   ├── models/
│   │   ├── Book.kt ✅
│   │   ├── Review.kt ✅
│   │   ├── UserBook.kt ✅
│   │   ├── UserPreference.kt ✅
│   │   └── BookRecommendation.kt ✅
│   ├── book/
│   │   └── BookRepositoryImpl.kt ✅
│   ├── review/
│   │   └── ReviewRepositoryImpl.kt ✅
│   └── preference/
│       └── UserPreferenceRepositoryImpl.kt ✅
├── domain/
│   ├── book/
│   │   └── BookRepository.kt ✅
│   ├── review/
│   │   └── ReviewRepository.kt ✅
│   └── preference/
│       └── UserPreferenceRepository.kt ✅
├── presentation/
│   └── features/
│       └── books/
│           ├── list/
│           │   ├── blocks/ ✅
│           │   └── compose/BookListScreen.kt ✅
│           ├── detail/ (TODO)
│           ├── add/
│           │   └── blocks/ ✅ (VM, models, reducer)
│           ├── recommendations/ (TODO)
│           └── library/ (TODO)
├── app/
│   ├── Route.kt ✅
│   └── SupabaseProvider.kt ✅
└── core/
    └── di/
        └── AppModule.kt ✅
```

## Notes

- All features follow the existing MVI pattern used in LoginVM/SignUpVM
- Supabase PostgreSQL is used as the backend
- Row Level Security (RLS) ensures users can only modify their own data
- The recommendation algorithm is basic but functional - can be enhanced with actual ML
- All models are serializable for network/database operations
- Error handling uses Kotlin Result type

## Next Steps

1. Create BookDetail screen to view book info and reviews
2. Create Recommendations screen using the AI algorithm
3. Create MyLibrary screen to manage reading lists
4. Create AddBook UI screen
5. Set up the Supabase database tables
6. Test the complete flow
7. Add polish: animations, loading states, error handling