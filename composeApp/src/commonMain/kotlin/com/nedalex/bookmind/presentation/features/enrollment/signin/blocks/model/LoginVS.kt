package com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.model

data class LoginVS(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)