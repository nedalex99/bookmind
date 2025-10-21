package com.nedalex.bookmind.domain.preference

interface PreferencesRepository {

    suspend fun saveUserPreferences(
        genres: List<String>,
        authorIds: List<String>,
        bookIds: List<String>,
        readingGoal: Int
    )

    suspend fun getUserPreferences(): UserPreferencesEntity?

    suspend fun hasCompletedSetup(): Boolean

    // Basic search methods (unchanged)
    suspend fun searchAuthors(query: String, limit: Int = 10): List<AuthorEntity>
    suspend fun searchBooks(query: String, limit: Int = 20): List<BookEntity>

    // NEW: Paginated smart recommendation methods with infinite scroll
    suspend fun getSmartAuthorRecommendations(
        genres: List<String>,
        offset: Int = 0,
        limit: Int = 10,
        useAI: Boolean = true
    ): PaginatedRecommendations<AuthorEntity>

    suspend fun getSmartBookRecommendations(
        genres: List<String>,
        authorIds: List<String> = emptyList(),
        offset: Int = 0,
        limit: Int = 10,
        useAI: Boolean = true
    ): PaginatedRecommendations<BookEntity>

    // OLD: Keep for backward compatibility
    suspend fun getPopularAuthors(limit: Int = 20): List<AuthorEntity>
    suspend fun getPopularBooks(limit: Int = 20): List<BookEntity>

    // Hybrid approach - save when selected
    suspend fun saveSelectedAuthor(author: AuthorEntity)
    suspend fun saveSelectedBook(book: BookEntity)
}

// NEW: Paginated result with metadata
data class PaginatedRecommendations<T>(
    val items: List<T>,
    val hasMore: Boolean,
    val nextOffset: Int,
    val tier: RecommendationTier
)

enum class RecommendationTier {
    AI_CURATED,      // 🌟 AI recommendations
    GENRE_FILTERED,  // 📚 Filtered by genre/author
    POPULAR          // ⭐ Popular fallback
}

// Domain entities
data class UserPreferencesEntity(
    val id: String,
    val userId: String,
    val favoriteGenres: List<String>,
    val favoriteAuthors: List<String>,
    val booksRead: List<String>,
    val readingGoal: Int,
    val completedAt: String?
)

data class AuthorEntity(
    val id: String,
    val name: String,
    val notableBooks: String,
    val isFromDatabase: Boolean = true
)

data class BookEntity(
    val id: String,
    val title: String,
    val author: String,
    val rating: Double,
    val pageCount: Int,
    val description: String,
    val similarBooks: List<String>,
    val coverUrl: String?,
    val isFromDatabase: Boolean = true
)