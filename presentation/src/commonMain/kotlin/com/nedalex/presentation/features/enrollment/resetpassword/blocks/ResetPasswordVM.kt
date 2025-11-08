package com.nedalex.presentation.features.enrollment.resetpassword.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.domain.auth.AuthRepository
import com.nedalex.presentation.architecture.BaseVM
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordAction
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordNavigation
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordResult
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordVS
import kotlinx.coroutines.launch

class ResetPasswordVM(
    private val authRepository: AuthRepository
) : BaseVM<ResetPasswordVS, ResetPasswordResult, ResetPasswordAction, ResetPasswordNavigation>(
    viewState = ResetPasswordVS(),
    reducer = ResetPasswordRR()
) {

    override fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.EmailChanged -> {
                onResult(ResetPasswordResult.EmailChanged(action.email))
            }

            ResetPasswordAction.ResetButtonClicked -> {
                resetPassword()
            }

            ResetPasswordAction.BackToLoginClicked -> {
                navigate(ResetPasswordNavigation.ToLogin)
            }
        }
    }

    private fun resetPassword() {
        val email = viewState.email

        // Validate
        val emailError = validateEmail(email)

        if (emailError != null) {
            onResult(ResetPasswordResult.ValidationError(emailError))
            return
        }

        viewModelScope.launch {
            onResult(ResetPasswordResult.Loading)

            try {
                authRepository.resetPassword(email)
                onResult(ResetPasswordResult.Success)
            } catch (e: Exception) {
                onResult(ResetPasswordResult.Error(e.message ?: "Password reset failed"))
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.contains("@") -> "Invalid email format"
            !email.contains(".") -> "Invalid email format"
            else -> null
        }
    }
}