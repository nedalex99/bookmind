package com.nedalex.bookmind.presentation.features.dashboard

import com.nedalex.bookmind.domain.preference.BookEntity

// ============================================
// VIEW STATE
// ============================================

data class DashboardVS(
    // User Info
    val userName: String = "",
    val greeting: String = "",

    // Currently Reading
    val currentlyReading: List<ReadingProgress> = emptyList(),

    // Recommendations
    val recommendations: List<BookRecommendation> = emptyList(),

    // Stats
    val totalBooksRead: Int = 0,
    val totalPagesRead: Int = 0,
    val totalHoursRead: Int = 0,

    // Friend Activity
    val friendActivities: List<FriendActivity> = emptyList(),

    // UI State
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasCompletedPreferences: Boolean = false
)

data class ReadingProgress(
    val book: BookEntity,
    val currentPage: Int,
    val totalPages: Int,
    val progressPercentage: Int
)

data class BookRecommendation(
    val book: BookEntity,
    val matchPercentage: Int,
    val reason: String
)

data class FriendActivity(
    val id: String,
    val userName: String,
    val userAvatar: String?,
    val activityType: ActivityType,
    val bookTitle: String,
    val timestamp: Long,
    val rating: Int? = null
)

enum class ActivityType {
    FINISHED,
    STARTED,
    RATED
}

// ============================================
// ACTIONS
// ============================================

sealed interface DashboardAction {
    data object LoadDashboard : DashboardAction
    data object RefreshDashboard : DashboardAction
    data class BookClicked(val bookId: String) : DashboardAction
    data class RecommendationClicked(val bookId: String) : DashboardAction
    data class FriendActivityClicked(val activityId: String) : DashboardAction
    data object ViewAllCurrentlyReading : DashboardAction
    data object ViewAllRecommendations : DashboardAction
    data object NavigateToDiscover : DashboardAction
    data object NavigateToLibrary : DashboardAction
    data object NavigateToProfile : DashboardAction
}

// ============================================
// RESULTS
// ============================================

sealed interface DashboardResult {
    data object Loading : DashboardResult
    data object Refreshing : DashboardResult
    data class DashboardLoaded(
        val userName: String,
        val greeting: String,
        val currentlyReading: List<ReadingProgress>,
        val recommendations: List<BookRecommendation>,
        val totalBooksRead: Int,
        val totalPagesRead: Int,
        val totalHoursRead: Int,
        val friendActivities: List<FriendActivity>,
        val hasCompletedPreferences: Boolean
    ) : DashboardResult
    data class Error(val message: String) : DashboardResult
}

// ============================================
// NAVIGATION EVENTS
// ============================================

sealed interface DashboardNavigation {
    data class ToBookDetail(val bookId: String) : DashboardNavigation
    data class ToFriendProfile(val userId: String) : DashboardNavigation
    data object ToCurrentlyReadingList : DashboardNavigation
    data object ToRecommendationsList : DashboardNavigation
    data object ToDiscover : DashboardNavigation
    data object ToLibrary : DashboardNavigation
    data object ToProfile : DashboardNavigation
}