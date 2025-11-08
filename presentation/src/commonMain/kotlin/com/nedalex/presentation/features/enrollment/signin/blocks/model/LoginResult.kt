package com.nedalex.presentation.features.enrollment.signin.blocks.model

sealed interface LoginResult {
    data object Loading : LoginResult
    data class EmailChanged(val email: String) : LoginResult
    data class PasswordChanged(val password: String) : LoginResult
    data class ValidationError(val emailError: String?, val passwordError: String?) : LoginResult
    data object Success : LoginResult
    data class Error(val message: String) : LoginResult
}