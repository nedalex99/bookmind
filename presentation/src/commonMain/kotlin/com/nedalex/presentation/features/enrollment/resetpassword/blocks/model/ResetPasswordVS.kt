package com.nedalex.presentation.features.enrollment.resetpassword.blocks.model

data class ResetPasswordVS(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)