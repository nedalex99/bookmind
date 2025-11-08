package com.nedalex.bookmind.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nedalex.core.Route
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.ResetPasswordVM
import com.nedalex.presentation.features.enrollment.resetpassword.compose.ResetPasswordScreen
import com.nedalex.presentation.features.enrollment.signin.blocks.LoginVM
import com.nedalex.presentation.features.enrollment.signin.compose.LoginScreen
import com.nedalex.presentation.features.enrollment.singup.blocks.SignUpVM
import com.nedalex.presentation.features.enrollment.singup.compose.SignUpScreen
import com.nedalex.presentation.theme.ReadingAppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
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
                composable<Route.ResetPassword> {
                    val vm = koinViewModel<ResetPasswordVM>()
                    ResetPasswordScreen(vm, navController)
                }
//                composable<Route.Preferences> {
//                    val vm = koinViewModel<PreferencesVM>()
//                    PreferencesScreen(vm, navController)
//                }
            }
//            navigation<Route.MainGraph>(
//                startDestination = Route.Dashboard
//            ) {
//
//            }
        }
    }
}