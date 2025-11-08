package com.nedalex.presentation.features.enrollment.resetpassword.blocks

import com.nedalex.presentation.architecture.reducer.BaseReducer
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordResult
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordVS

class ResetPasswordRR : BaseReducer<ResetPasswordVS, ResetPasswordResult> {
    override fun reduce(
        viewState: ResetPasswordVS,
        result: ResetPasswordResult
    ): ResetPasswordVS {
        return when (result) {
            is ResetPasswordResult.Loading -> viewState.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            is ResetPasswordResult.EmailChanged -> viewState.copy(
                email = result.email,
                emailError = null,
                error = null,
                isSuccess = false
            )

            is ResetPasswordResult.ValidationError -> viewState.copy(
                isLoading = false,
                emailError = result.emailError
            )

            is ResetPasswordResult.Success -> viewState.copy(
                isLoading = false,
                error = null,
                isSuccess = true
            )

            is ResetPasswordResult.Error -> viewState.copy(
                isLoading = false,
                error = result.message,
                isSuccess = false
            )
        }
    }
}