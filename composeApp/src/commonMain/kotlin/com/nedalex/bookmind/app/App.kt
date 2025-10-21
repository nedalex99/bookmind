package com.nedalex.bookmind.app

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nedalex.bookmind.presentation.features.dashboard.DashboardScreen
import com.nedalex.bookmind.presentation.features.dashboard.blocks.DashboardVM
import com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks.PreferencesScreen
import com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks.PreferencesVM
import com.nedalex.bookmind.presentation.features.enrollment.signin.compose.LoginScreen
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.LoginVM
import com.nedalex.bookmind.presentation.features.enrollment.singup.blocks.SignUpVM
import com.nedalex.bookmind.presentation.features.enrollment.singup.compose.SignUpScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.nedalex.bookmind.presentation.theme.ReadingAppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    ReadingAppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Route.EnrollmentGraph
        ) {
            navigation<Route.EnrollmentGraph>(
                startDestination = Route.SignIn
            ) {
                composable<Route.SignIn> {
                    val vm = koinViewModel<LoginVM>()
                    LoginScreen(vm, navController)
                }
                composable<Route.SignUp> {
                    val vm = koinViewModel<SignUpVM>()
                    SignUpScreen(vm, navController)
                }
                composable<Route.Preferences> {
                    val vm = koinViewModel<PreferencesVM>()
                    PreferencesScreen(vm, navController)
                }
            }
            navigation<Route.MainGraph>(
                startDestination = Route.Dashboard
            ) {
                composable<Route.Dashboard> {
                    val vm = koinViewModel<DashboardVM>()
                    DashboardScreen(vm, navController)
                }
            }
        }
    }
}