package com.nedalex.presentation.features.enrollment.resetpassword.blocks.model

sealed interface ResetPasswordNavigation {
    data object ToLogin : ResetPasswordNavigation
}