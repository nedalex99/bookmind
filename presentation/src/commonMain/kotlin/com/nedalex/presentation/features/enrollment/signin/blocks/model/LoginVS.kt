package com.nedalex.presentation.features.enrollment.signin.blocks.model

import com.nedalex.core.Platform

data class LoginVS(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val platform: Platform
)