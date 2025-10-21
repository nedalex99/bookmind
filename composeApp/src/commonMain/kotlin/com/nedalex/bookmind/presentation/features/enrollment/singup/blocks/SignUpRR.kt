package com.nedalex.bookmind.presentation.features.enrollment.singup.blocks

import com.nedalex.bookmind.architecture.blocks.reducer.BaseReducer
import com.nedalex.bookmind.presentation.features.enrollment.singup.blocks.model.SignUpResult
import com.nedalex.bookmind.presentation.features.enrollment.singup.blocks.model.SignUpVS

class SignUpRR : BaseReducer<SignUpVS, SignUpResult> {
    override fun reduce(
        viewState: SignUpVS,
        result: SignUpResult
    ): SignUpVS {
        return when (result) {
            is SignUpResult.Loading -> viewState.copy(
                isLoading = true,
                error = null
            )

            is SignUpResult.NameChanged -> viewState.copy(
                name = result.name,
                nameError = null,
                error = null
            )

            is SignUpResult.EmailChanged -> viewState.copy(
                email = result.email,
                emailError = null,
                error = null
            )

            is SignUpResult.PasswordChanged -> viewState.copy(
                password = result.password,
                passwordError = null,
                error = null
            )

            is SignUpResult.ConfirmPasswordChanged -> viewState.copy(
                confirmPassword = result.password,
                confirmPasswordError = null,
                error = null
            )

            is SignUpResult.TermsAcceptedChanged -> viewState.copy(
                acceptedTerms = result.accepted
            )

            is SignUpResult.ValidationError -> viewState.copy(
                isLoading = false,
                nameError = result.nameError,
                emailError = result.emailError,
                passwordError = result.passwordError,
                confirmPasswordError = result.confirmPasswordError
            )

            is SignUpResult.Success -> viewState.copy(
                isLoading = false,
                error = null
            )

            is SignUpResult.Error -> viewState.copy(
                isLoading = false,
                error = result.message
            )
        }
    }
}