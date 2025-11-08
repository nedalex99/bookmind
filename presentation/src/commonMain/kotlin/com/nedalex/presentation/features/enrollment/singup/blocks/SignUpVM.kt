package com.nedalex.presentation.features.enrollment.singup.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.core.getPlatform
import com.nedalex.domain.auth.AuthRepository
import com.nedalex.presentation.architecture.BaseVM
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpAction
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpNavigation
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpResult
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpVS
import kotlinx.coroutines.launch

class SignUpVM(
    private val authRepository: AuthRepository
) : BaseVM<SignUpVS, SignUpResult, SignUpAction, SignUpNavigation>(
    viewState = SignUpVS(
        platform = getPlatform()
    ),
    reducer = SignUpRR()
) {
    override fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.NameChanged -> {
                onResult(SignUpResult.NameChanged(action.name))
            }

            is SignUpAction.EmailChanged -> {
                onResult(SignUpResult.EmailChanged(action.email))
            }

            is SignUpAction.PasswordChanged -> {
                onResult(SignUpResult.PasswordChanged(action.password))
            }

            is SignUpAction.ConfirmPasswordChanged -> {
                onResult(SignUpResult.ConfirmPasswordChanged(action.password))
            }

            is SignUpAction.TermsAcceptedChanged -> {
                onResult(SignUpResult.TermsAcceptedChanged(action.accepted))
            }

            SignUpAction.SignUpButtonClicked -> {
                signUp()
            }

            SignUpAction.SignInButtonClicked -> {
                navigate(SignUpNavigation.ToSignIn)
            }

            SignUpAction.GoogleSignUpClicked -> {
                signUpWithGoogle()
            }

            SignUpAction.AppleSignUpClicked -> {
                signUpWithApple()
            }
        }
    }

    private fun signUp() {
        val name = viewState.name
        val email = viewState.email
        val password = viewState.password
        val confirmPassword = viewState.confirmPassword

        // Validate
        val nameError = validateName(name)
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmPasswordError = validateConfirmPassword(password, confirmPassword)

        if (nameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null) {
            onResult(SignUpResult.ValidationError(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            ))
            return
        }

        if (!viewState.acceptedTerms) {
            onResult(SignUpResult.Error("Please accept the terms and conditions"))
            return
        }

        viewModelScope.launch {
            onResult(SignUpResult.Loading)

            try {
                authRepository.signUpWithEmailAndPassword(email, password, name)
                onResult(SignUpResult.Success)
                navigate(SignUpNavigation.ToPreferences)
            } catch (e: Exception) {
                onResult(SignUpResult.Error(e.message ?: "Sign up failed"))
            }
        }
    }

    private fun signUpWithGoogle() {
        viewModelScope.launch {
            onResult(SignUpResult.Loading)

            try {
//                authRepository.signInWithGoogle()
                onResult(SignUpResult.Success)
                navigate(SignUpNavigation.ToPreferences)
            } catch (e: Exception) {
                onResult(SignUpResult.Error(e.message ?: "Google sign up failed"))
            }
        }
    }

    private fun signUpWithApple() {
        viewModelScope.launch {
            onResult(SignUpResult.Loading)

            try {
//                authRepository.signInWithApple()
                onResult(SignUpResult.Success)
                navigate(SignUpNavigation.ToPreferences)
            } catch (e: Exception) {
                onResult(SignUpResult.Error(e.message ?: "Apple sign up failed"))
            }
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
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

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}