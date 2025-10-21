package com.nedalex.bookmind.data.book

import com.nedalex.bookmind.domain.book.BookRepository
import com.nedalex.bookmind.domain.book.ReadingStats
import com.nedalex.bookmind.domain.book.User
import com.nedalex.bookmind.domain.book.UserRepository
import com.nedalex.bookmind.domain.preference.BookEntity
import com.nedalex.bookmind.presentation.features.dashboard.ActivityType
import com.nedalex.bookmind.presentation.features.dashboard.BookRecommendation
import com.nedalex.bookmind.presentation.features.dashboard.FriendActivity
import com.nedalex.bookmind.presentation.features.dashboard.ReadingProgress
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// ============================================
// BOOK REPOSITORY IMPLEMENTATION
// ============================================

class BookRepositoryImpl(
    private val supabase: SupabaseClient
) : BookRepository {

    override suspend fun getCurrentlyReading(): List<ReadingProgress> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return emptyList()

        // Use RPC to handle enum properly
        val userBooks = try {
            supabase.postgrest.rpc(
                function = "get_currently_reading",
                parameters = mapOf("user_uuid" to JsonPrimitive(userId))
            ).decodeList<UserBookDto>()
        } catch (e: Exception) {
            println("Error fetching currently reading books: ${e.message}")
            e.printStackTrace()
            emptyList()
        }

        return userBooks.map { userBook ->
            val book = getBookById(userBook.bookId)
            ReadingProgress(
                book = book,
                currentPage = userBook.currentPage,
                totalPages = book.pageCount,
                progressPercentage = userBook.progressPercentage
            )
        }
    }

    override suspend fun getRecommendations(limit: Int): List<BookRecommendation> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return emptyList()

        // Get user preferences
        val preferences = supabase.postgrest["user_preferences"]
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeSingleOrNull<UserPreferencesDto>()

        // If no preferences or not completed, return empty
        if (preferences == null || preferences.completedAt == null) {
            return emptyList()
        }

        // Get books based on favorite genres and authors
        val books = supabase.postgrest["books"]
            .select {
                filter {
                    // Filter by favorite genres if available
                    if (preferences.favoriteGenres.isNotEmpty()) {
                        or {
                            preferences.favoriteGenres.forEach { genre ->
                                ilike("genre", "%$genre%")
                            }
                        }
                    }
                }
                order("rating", order = Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<BookDto>()

        return books.map { dto ->
            BookRecommendation(
                book = dto.toDomain(),
                matchPercentage = calculateMatchPercentage(dto, preferences),
                reason = buildRecommendationReason(dto, preferences)
            )
        }.sortedByDescending { it.matchPercentage }
    }

    override suspend fun getReadingStats(): ReadingStats {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return ReadingStats(0, 0, 0)

        // Get user data
        val user = supabase.postgrest["users"]
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<UserDto>()
            ?: return ReadingStats(0, 0, 0)

        // Calculate total pages from reading sessions
        val sessions = supabase.postgrest["reading_sessions"]
            .select(columns = Columns.list("pages_read", "duration_minutes")) {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<ReadingSessionDto>()

        val totalPages = sessions.sumOf { it.pagesRead }
        val totalMinutes = sessions.sumOf { it.durationMinutes ?: 0 }
        val totalHours = totalMinutes / 60

        return ReadingStats(
            totalBooksRead = user.booksReadCount,
            totalPagesRead = totalPages,
            totalHoursRead = totalHours
        )
    }

    private suspend fun getBookById(bookId: String): BookEntity {
        val book = supabase.postgrest["books"]
            .select {
                filter {
                    eq("id", bookId)
                }
            }
            .decodeSingle<BookDto>()

        return book.toDomain()
    }

    private fun calculateMatchPercentage(book: BookDto, preferences: UserPreferencesDto): Int {
        var matchScore = 0
        var totalFactors = 0

        // Check genre match (40% weight)
        if (preferences.favoriteGenres.isNotEmpty()) {
            totalFactors += 4
            val genreMatches = preferences.favoriteGenres.count { genre ->
                book.genre?.contains(genre, ignoreCase = true) == true
            }
            matchScore += (genreMatches * 4) / preferences.favoriteGenres.size.coerceAtLeast(1)
        }

        // Check author match (30% weight)
        if (preferences.favoriteAuthors.isNotEmpty()) {
            totalFactors += 3
            if (book.authorId != null && preferences.favoriteAuthors.contains(book.authorId)) {
                matchScore += 3
            }
        }

        // Check rating (30% weight)
        totalFactors += 3
        val ratingScore = ((book.rating ?: 0.0) / 5.0 * 3).toInt()
        matchScore += ratingScore

        // Calculate percentage (85-98 range for recommendations)
        return if (totalFactors > 0) {
            val percentage = (matchScore.toFloat() / totalFactors.toFloat() * 100).toInt()
            percentage.coerceIn(85, 98)
        } else {
            90 // Default match percentage
        }
    }

    private fun buildRecommendationReason(book: BookDto, preferences: UserPreferencesDto): String {
        return when {
            book.authorId != null && preferences.favoriteAuthors.contains(book.authorId) -> {
                "You loved other books by ${book.author}"
            }
            preferences.favoriteGenres.any { book.genre?.contains(it, ignoreCase = true) == true } -> {
                val matchedGenre = preferences.favoriteGenres.first {
                    book.genre?.contains(it, ignoreCase = true) == true
                }
                "Based on your love for $matchedGenre"
            }
            else -> "Highly rated in your favorite categories"
        }
    }
}

// ============================================
// USER REPOSITORY IMPLEMENTATION
// ============================================

class UserRepositoryImpl(
    private val supabase: SupabaseClient
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        val authUser = supabase.auth.currentUserOrNull() ?: return null

        val userProfile = supabase.postgrest["users"]
            .select {
                filter {
                    eq("id", authUser.id)
                }
            }
            .decodeSingleOrNull<UserDto>()

        return User(
            id = authUser.id,
            name = userProfile?.fullName ?: authUser.email?.substringBefore("@") ?: "Guest",
            email = authUser.email ?: "",
            avatar = userProfile?.avatarUrl
        )
    }

    override suspend fun getFriendActivities(limit: Int): List<FriendActivity> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return emptyList()

        // Get user's following list
        val follows = supabase.postgrest["follows"]
            .select(columns = Columns.list("following_id")) {
                filter {
                    eq("follower_id", userId)
                }
            }
            .decodeList<FollowDto>()

        val followingIds = follows.map { it.followingId }
        if (followingIds.isEmpty()) return emptyList()

        // Get recent activities from followed users
        // Using your actual enum values: started_reading, book_completed, review_posted
        val activities = supabase.postgrest["activities"]
            .select {
                filter {
                    isIn("user_id", followingIds)
                }
                order("created_at", order = Order.DESCENDING)
                limit(limit.toLong())
            }
            .decodeList<ActivityDto>()

        return activities.mapNotNull { dto ->
            try {
                // Map your enum values to our ActivityType
                val activityType = when (dto.activityType) {
                    "book_completed" -> ActivityType.FINISHED
                    "started_reading" -> ActivityType.STARTED
                    "review_posted" -> ActivityType.RATED
                    else -> return@mapNotNull null // Skip other activity types
                }

                val userName = getUserName(dto.userId)
                val bookTitle = dto.bookId?.let { getBookTitle(it) } ?: return@mapNotNull null

                FriendActivity(
                    id = dto.id,
                    userName = userName,
                    userAvatar = null,
                    activityType = activityType,
                    bookTitle = bookTitle,
                    timestamp = parseTimestamp(dto.createdAt),
                    rating = dto.metadata?.get("rating")?.toIntOrNull()
                )
            } catch (e: Exception) {
                println("Error parsing activity: ${e.message}")
                null
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseTimestamp(createdAt: String): Long {
        return try {
            // If it's already a timestamp (milliseconds)
            createdAt.toLongOrNull() ?: run {
                // If it's a timestamp string, parse it
                Instant.parse(createdAt).toEpochMilliseconds()
            }
        } catch (e: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }

    private suspend fun getUserName(userId: String): String {
        val user = supabase.postgrest["users"]
            .select(columns = Columns.list("full_name", "email")) {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<UserDto>()

        return user?.fullName ?: user?.email?.substringBefore("@") ?: "User"
    }

    private suspend fun getBookTitle(bookId: String): String {
        val book = supabase.postgrest["books"]
            .select(columns = Columns.list("title")) {
                filter {
                    eq("id", bookId)
                }
            }
            .decodeSingleOrNull<BookDto>()

        return book?.title ?: "Unknown Book"
    }
}

// ============================================
// DATA TRANSFER OBJECTS (DTOs)
// ============================================

@Serializable
private data class UserBookDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("book_id") val bookId: String,
    val status: String,
    @SerialName("current_page") val currentPage: Int = 0,
    @SerialName("progress_percentage") val progressPercentage: Int = 0,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("completed_at") val completedAt: String? = null,
    @SerialName("user_rating") val userRating: Double? = null
)

@Serializable
private data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    @SerialName("author_id") val authorId: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    @SerialName("page_count") val pageCount: Int = 0,
    @SerialName("publication_year") val publicationYear: Int? = null,
    val genre: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("similar_books") val similarBooks: List<String> = emptyList(),
    val isbn: String? = null
) {
    fun toDomain() = BookEntity(
        id = id,
        title = title,
        author = author,
        rating = rating ?: 0.0,
        pageCount = pageCount,
        description = description ?: "",
        similarBooks = if (genre != null) listOf(genre) else similarBooks,
        coverUrl = coverUrl
    )
}

@Serializable
private data class UserDto(
    val id: String,
    val email: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val bio: String? = null,
    @SerialName("books_read_count") val booksReadCount: Int = 0,
    @SerialName("reading_goal") val readingGoal: Int = 24,
    @SerialName("current_streak") val currentStreak: Int = 0,
    @SerialName("longest_streak") val longestStreak: Int = 0
)

@Serializable
private data class UserPreferencesDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("favorite_genres") val favoriteGenres: List<String> = emptyList(),
    @SerialName("favorite_authors") val favoriteAuthors: List<String> = emptyList(),
    @SerialName("books_read") val booksRead: List<String> = emptyList(),
    @SerialName("completed_at") val completedAt: String? = null
)

@Serializable
private data class ReadingSessionDto(
    @SerialName("pages_read") val pagesRead: Int,
    @SerialName("duration_minutes") val durationMinutes: Int? = null
)

@Serializable
private data class FollowDto(
    @SerialName("following_id") val followingId: String
)

@Serializable
private data class ActivityDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("activity_type") val activityType: String,
    @SerialName("book_id") val bookId: String? = null,
    @SerialName("review_id") val reviewId: String? = null,
    val metadata: Map<String, String>? = null,
    @SerialName("created_at") val createdAt: String
)