package com.nedalex.bookmind.presentation.features.enrollment.singup.blocks.model

// ============================================
// VIEW STATE
// ============================================

data class SignUpVS(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val acceptedTerms: Boolean = false
)

// ============================================
// ACTIONS
// ============================================

sealed interface SignUpAction {
    data class NameChanged(val name: String) : SignUpAction
    data class EmailChanged(val email: String) : SignUpAction
    data class PasswordChanged(val password: String) : SignUpAction
    data class ConfirmPasswordChanged(val password: String) : SignUpAction
    data class TermsAcceptedChanged(val accepted: Boolean) : SignUpAction
    data object SignUpButtonClicked : SignUpAction
    data object SignInButtonClicked : SignUpAction
    data object GoogleSignUpClicked : SignUpAction
    data object AppleSignUpClicked : SignUpAction
}

// ============================================
// RESULTS
// ============================================

sealed interface SignUpResult {
    data object Loading : SignUpResult
    data class NameChanged(val name: String) : SignUpResult
    data class EmailChanged(val email: String) : SignUpResult
    data class PasswordChanged(val password: String) : SignUpResult
    data class ConfirmPasswordChanged(val password: String) : SignUpResult
    data class TermsAcceptedChanged(val accepted: Boolean) : SignUpResult
    data class ValidationError(
        val nameError: String?,
        val emailError: String?,
        val passwordError: String?,
        val confirmPasswordError: String?
    ) : SignUpResult

    data object Success : SignUpResult
    data class Error(val message: String) : SignUpResult
}

// ============================================
// NAVIGATION EVENTS
// ============================================

sealed interface SignUpNavigation {
    data object ToSignIn : SignUpNavigation
    data object ToPreferences : SignUpNavigation
}