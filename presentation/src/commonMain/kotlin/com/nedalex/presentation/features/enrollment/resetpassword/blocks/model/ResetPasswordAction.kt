package com.nedalex.presentation.features.enrollment.resetpassword.blocks.model

sealed interface ResetPasswordAction {
    data object ResetButtonClicked : ResetPasswordAction
    data object BackToLoginClicked : ResetPasswordAction
    data class EmailChanged(val email: String) : ResetPasswordAction
}