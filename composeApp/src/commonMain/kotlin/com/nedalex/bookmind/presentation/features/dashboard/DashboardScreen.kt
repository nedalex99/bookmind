package com.nedalex.bookmind.presentation.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nedalex.bookmind.architecture.blocks.navigation.NavigationHandler
import com.nedalex.bookmind.presentation.features.dashboard.blocks.DashboardVM
import com.nedalex.bookmind.presentation.theme.Stone50
import com.nedalex.bookmind.presentation.theme.Stone200

@Composable
fun DashboardScreen(
    viewModel: DashboardVM,
    navController: NavController
) {
    val viewState = viewModel.viewState

    // Handle navigation
    NavigationHandler(
        navController = navController,
        navigationFlow = viewModel.navigation,
        onNavigate = { event ->
            when (event) {
                is DashboardNavigation.ToBookDetail -> TODO()
                DashboardNavigation.ToCurrentlyReadingList -> TODO()
                DashboardNavigation.ToDiscover -> TODO()
                is DashboardNavigation.ToFriendProfile -> TODO()
                DashboardNavigation.ToLibrary -> TODO()
                DashboardNavigation.ToProfile -> TODO()
                DashboardNavigation.ToRecommendationsList -> TODO()
            }
        }
    )

    Scaffold(
        containerColor = Stone50,
        bottomBar = {
            DashboardBottomNav(
                currentRoute = "home",
                onNavigate = { route ->
                    when (route) {
                        "discover" -> viewModel.onAction(DashboardAction.NavigateToDiscover)
                        "library" -> viewModel.onAction(DashboardAction.NavigateToLibrary)
                        "profile" -> viewModel.onAction(DashboardAction.NavigateToProfile)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                viewState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                viewState.error != null -> {
                    ErrorView(
                        message = viewState.error ?: "Unknown error",
                        onRetry = { viewModel.onAction(DashboardAction.LoadDashboard) }
                    )
                }

                else -> {
                    DashboardContent(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        viewState = viewState,
                        onAction = viewModel::onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier = Modifier,
    viewState: DashboardVS,
    onAction: (DashboardAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Stone50)
    ) {
        // Header
        DashboardHeader(
            greeting = viewState.greeting,
            userName = viewState.userName
        )

        Divider(color = Stone200, thickness = 1.dp)

        // Currently Reading Section
        if (viewState.currentlyReading.isNotEmpty()) {
            CurrentlyReadingSection(
                books = viewState.currentlyReading,
                onViewAll = { onAction(DashboardAction.ViewAllCurrentlyReading) },
                onBookClick = { bookId ->
                    onAction(DashboardAction.BookClicked(bookId))
                }
            )
        } else {
            EmptyCurrentlyReadingSection(onAction)
        }

        Divider(color = Stone200, thickness = 1.dp)

        // Recommendations Section
        if (viewState.recommendations.isNotEmpty()) {
            RecommendationsSection(
                recommendations = viewState.recommendations,
                onSeeAll = { onAction(DashboardAction.ViewAllRecommendations) },
                onBookClick = { bookId ->
                    onAction(DashboardAction.RecommendationClicked(bookId))
                }
            )
        } else {
            EmptyRecommendationsSection()
        }
        Divider(color = Stone200, thickness = 1.dp)


        // Stats Section
        StatsSection(
            totalBooks = viewState.totalBooksRead,
            totalPages = viewState.totalPagesRead,
            totalHours = viewState.totalHoursRead
        )
        Divider(color = Stone200, thickness = 1.dp)

        // Friend Activity Section
        if (viewState.friendActivities.isNotEmpty()) {
            FriendActivitySection(
                activities = viewState.friendActivities,
                onActivityClick = { activityId ->
                    onAction(DashboardAction.FriendActivityClicked(activityId))
                }
            )
        } else {
            EmptyFriendActivitySection(onAction)
        }
        Divider(color = Stone200, thickness = 1.dp)

        // Bottom spacing for bottom nav
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}