package com.nedalex.presentation.features.enrollment.signin.blocks.model

sealed interface LoginAction {
    data object SignInButtonClicked : LoginAction
    data object SignUpButtonClicked : LoginAction
    data object ForgotPasswordClicked : LoginAction
    data object GoogleSignInClicked : LoginAction
    data object AppleSignInClicked : LoginAction
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
}
