package com.nedalex.presentation.features.enrollment.signin.blocks.model

sealed interface LoginNavigation {
    data object ToSignUp : LoginNavigation
    data object ToResetPassword : LoginNavigation
    data object ToPreferences : LoginNavigation
    data object ToHome : LoginNavigation
}