package com.nedalex.presentation.architecture.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> NavigationHandler(
    navController: NavController,
    navigationFlow: Flow<T>,
    onNavigate: (T) -> Unit
) {
    LaunchedEffect(Unit) {
        navigationFlow.collect { event ->
            onNavigate(event)
        }
    }
}
//