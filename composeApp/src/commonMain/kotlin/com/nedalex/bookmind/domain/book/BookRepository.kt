package com.nedalex.bookmind.domain.book

import com.nedalex.bookmind.presentation.features.dashboard.BookRecommendation
import com.nedalex.bookmind.presentation.features.dashboard.FriendActivity
import com.nedalex.bookmind.presentation.features.dashboard.ReadingProgress

// ============================================
// BOOK REPOSITORY - Add these methods
// ============================================

interface BookRepository {

    // ... existing methods ...

    /**
     * Get books that user is currently reading
     */
    suspend fun getCurrentlyReading(): List<ReadingProgress>

    /**
     * Get AI-powered book recommendations
     */
    suspend fun getRecommendations(limit: Int = 10): List<BookRecommendation>

    /**
     * Get user's reading statistics
     */
    suspend fun getReadingStats(): ReadingStats
}

data class ReadingStats(
    val totalBooksRead: Int,
    val totalPagesRead: Int,
    val totalHoursRead: Int
)

// ============================================
// USER REPOSITORY - Add these methods
// ============================================

interface UserRepository {

    // ... existing methods ...

    /**
     * Get current authenticated user
     */
    suspend fun getCurrentUser(): User?

    /**
     * Get friend activities (what friends are reading)
     */
    suspend fun getFriendActivities(limit: Int = 5): List<FriendActivity>
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String?
)

// ============================================
// PREFERENCES REPOSITORY - Add this method
// ============================================

interface PreferencesRepository {

    // ... existing methods ...

    /**
     * Get user preferences
     * Returns null if user hasn't set preferences yet
     */
    suspend fun getUserPreferences(): UserPreferences?
}

data class UserPreferences(
    val userId: String,
    val favoriteGenres: List<String>,
    val favoriteAuthors: List<String>,
    val booksRead: List<String>,
    val readingGoal: Int
)