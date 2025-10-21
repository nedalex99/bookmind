package com.nedalex.bookmind.presentation.features.dashboard.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.bookmind.architecture.blocks.BaseVM
import com.nedalex.bookmind.domain.book.BookRepository
import com.nedalex.bookmind.domain.book.UserRepository
import com.nedalex.bookmind.domain.preference.PreferencesRepository
import com.nedalex.bookmind.presentation.features.dashboard.DashboardAction
import com.nedalex.bookmind.presentation.features.dashboard.DashboardNavigation
import com.nedalex.bookmind.presentation.features.dashboard.DashboardRR
import com.nedalex.bookmind.presentation.features.dashboard.DashboardResult
import com.nedalex.bookmind.presentation.features.dashboard.DashboardVS
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class DashboardVM(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseVM<DashboardVS, DashboardResult, DashboardAction, DashboardNavigation>(
    viewState = DashboardVS(),
    reducer = DashboardRR()
) {

    init {
        loadDashboard()
    }

    override fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadDashboard -> {
                loadDashboard()
            }

            is DashboardAction.RefreshDashboard -> {
                refreshDashboard()
            }

            is DashboardAction.BookClicked -> {
                navigate(DashboardNavigation.ToBookDetail(action.bookId))
            }

            is DashboardAction.RecommendationClicked -> {
                navigate(DashboardNavigation.ToBookDetail(action.bookId))
            }

            is DashboardAction.FriendActivityClicked -> {
                navigate(DashboardNavigation.ToFriendProfile(action.activityId))
            }

            is DashboardAction.ViewAllCurrentlyReading -> {
                navigate(DashboardNavigation.ToCurrentlyReadingList)
            }

            is DashboardAction.ViewAllRecommendations -> {
                navigate(DashboardNavigation.ToRecommendationsList)
            }

            is DashboardAction.NavigateToDiscover -> {
                navigate(DashboardNavigation.ToDiscover)
            }

            is DashboardAction.NavigateToLibrary -> {
                navigate(DashboardNavigation.ToLibrary)
            }

            is DashboardAction.NavigateToProfile -> {
                navigate(DashboardNavigation.ToProfile)
            }
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            onResult(DashboardResult.Loading)
            try {
                loadDashboardData()
            } catch (e: Exception) {
                onResult(DashboardResult.Error(e.message ?: "Failed to load dashboard"))
            }
        }
    }

    private fun refreshDashboard() {
        viewModelScope.launch {
            onResult(DashboardResult.Refreshing)
            try {
                loadDashboardData()
            } catch (e: Exception) {
                onResult(DashboardResult.Error(e.message ?: "Failed to refresh dashboard"))
            }
        }
    }

    private suspend fun loadDashboardData() {
        // Get user info
        val user = userRepository.getCurrentUser()
        val userName = user?.name ?: "Guest"
        val greeting = getGreeting()

        // Check if preferences are completed
        val preferences = preferencesRepository.getUserPreferences()
        val hasCompletedPreferences = preferences != null

        // Get currently reading books
        val currentlyReading = bookRepository.getCurrentlyReading()

        // Get recommendations (only if preferences exist)
        val recommendations = if (hasCompletedPreferences) {
            bookRepository.getRecommendations(limit = 10)
        } else {
            emptyList()
        }

        // Get reading stats
        val stats = bookRepository.getReadingStats()

        // Get friend activities
        val friendActivities = userRepository.getFriendActivities(limit = 5)

        onResult(
            DashboardResult.DashboardLoaded(
                userName = userName,
                greeting = greeting,
                currentlyReading = currentlyReading,
                recommendations = recommendations,
                totalBooksRead = stats.totalBooksRead,
                totalPagesRead = stats.totalPagesRead,
                totalHoursRead = stats.totalHoursRead,
                friendActivities = friendActivities,
                hasCompletedPreferences = hasCompletedPreferences
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun getGreeting(): String {
        val currentHour = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .hour

        return when (currentHour) {
            in 0..11 -> "GOOD MORNING"
            in 12..17 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }
    }
}