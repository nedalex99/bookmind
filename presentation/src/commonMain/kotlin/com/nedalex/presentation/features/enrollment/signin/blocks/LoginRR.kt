package com.nedalex.presentation.features.enrollment.signin.blocks

import com.nedalex.presentation.architecture.reducer.BaseReducer
import com.nedalex.presentation.features.enrollment.signin.blocks.model.LoginResult
import com.nedalex.presentation.features.enrollment.signin.blocks.model.LoginVS

class LoginRR : BaseReducer<LoginVS, LoginResult> {
    override fun reduce(
        viewState: LoginVS,
        result: LoginResult
    ): LoginVS {
        return when (result) {
            is LoginResult.Loading -> viewState.copy(
                isLoading = true,
                error = null
            )

            is LoginResult.EmailChanged -> viewState.copy(
                email = result.email,
                emailError = null,
                error = null
            )

            is LoginResult.PasswordChanged -> viewState.copy(
                password = result.password,
                passwordError = null,
                error = null
            )

            is LoginResult.ValidationError -> viewState.copy(
                isLoading = false,
                emailError = result.emailError,
                passwordError = result.passwordError
            )

            is LoginResult.Success -> viewState.copy(
                isLoading = false,
                error = null
            )

            is LoginResult.Error -> viewState.copy(
                isLoading = false,
                error = result.message
            )
        }
    }
}