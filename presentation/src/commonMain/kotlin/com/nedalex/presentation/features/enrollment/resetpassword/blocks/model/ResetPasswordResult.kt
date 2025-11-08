package com.nedalex.presentation.features.enrollment.resetpassword.blocks.model

sealed interface ResetPasswordResult {
    data object Loading : ResetPasswordResult
    data class EmailChanged(val email: String) : ResetPasswordResult
    data class ValidationError(val emailError: String?) : ResetPasswordResult
    data object Success : ResetPasswordResult
    data class Error(val message: String) : ResetPasswordResult
}