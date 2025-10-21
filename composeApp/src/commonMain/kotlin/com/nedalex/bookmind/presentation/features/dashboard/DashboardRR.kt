package com.nedalex.bookmind.presentation.features.dashboard

import com.nedalex.bookmind.architecture.blocks.reducer.BaseReducer

class DashboardRR : BaseReducer<DashboardVS, DashboardResult> {
    override fun reduce(
        viewState: DashboardVS,
        result: DashboardResult
    ): DashboardVS {
        return when (result) {
            is DashboardResult.Loading -> viewState.copy(
                isLoading = true,
                error = null
            )

            is DashboardResult.Refreshing -> viewState.copy(
                isRefreshing = true,
                error = null
            )

            is DashboardResult.DashboardLoaded -> viewState.copy(
                userName = result.userName,
                greeting = result.greeting,
                currentlyReading = result.currentlyReading,
                recommendations = result.recommendations,
                totalBooksRead = result.totalBooksRead,
                totalPagesRead = result.totalPagesRead,
                totalHoursRead = result.totalHoursRead,
                friendActivities = result.friendActivities,
                hasCompletedPreferences = result.hasCompletedPreferences,
                isLoading = false,
                isRefreshing = false,
                error = null
            )

            is DashboardResult.Error -> viewState.copy(
                isLoading = false,
                isRefreshing = false,
                error = result.message
            )
        }
    }
}