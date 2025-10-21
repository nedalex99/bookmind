package com.nedalex.bookmind.presentation.features.enrollment.signin.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.bookmind.architecture.blocks.BaseVM
import com.nedalex.bookmind.domain.auth.AuthRepository
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.model.LoginAction
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.model.LoginNavigation
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.model.LoginResult
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.model.LoginVS
import kotlinx.coroutines.launch

class LoginVM(
    private val authRepository: AuthRepository
) : BaseVM<LoginVS, LoginResult, LoginAction, LoginNavigation>(
    viewState = LoginVS(),
    reducer = LoginRR()
) {
    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> {
                onResult(LoginResult.EmailChanged(action.email))
            }

            is LoginAction.PasswordChanged -> {
                onResult(LoginResult.PasswordChanged(action.password))
            }

            LoginAction.SignInButtonClicked -> {
                signIn()
            }

            LoginAction.SignUpButtonClicked -> {
                navigate(LoginNavigation.ToSignUp)
            }

            LoginAction.ForgotPasswordClicked -> {
//                navigate(LoginNavigation.ToForgotPassword)
            }

            LoginAction.GoogleSignInClicked -> {
                signInWithGoogle()
            }

            LoginAction.AppleSignInClicked -> {
                signInWithApple()
            }
        }
    }

    private fun signIn() {
        val email = viewState.email
        val password = viewState.password

        // Validate
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        if (emailError != null || passwordError != null) {
            onResult(LoginResult.ValidationError(emailError, passwordError))
            return
        }

        viewModelScope.launch {
            onResult(LoginResult.Loading)

            try {
                authRepository.signInWithEmailAndPassword(email, password)
//                navigate(LoginNavigation.ToPreferences)
                navigate(LoginNavigation.ToHome)
            } catch (e: Exception) {
                onResult(LoginResult.Error(e.message ?: "Sign in failed"))
            }
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            onResult(LoginResult.Loading)

            try {
//                authRepository.signInWithGoogle()
                onResult(LoginResult.Success)
//                navigate(LoginNavigation.ToHome)
            } catch (e: Exception) {
                onResult(LoginResult.Error(e.message ?: "Google sign in failed"))
            }
        }
    }

    private fun signInWithApple() {
        viewModelScope.launch {
            onResult(LoginResult.Loading)

            try {
//                authRepository.signInWithApple()
                onResult(LoginResult.Success)
//                navigate(LoginNavigation.ToHome)
            } catch (e: Exception) {
                onResult(LoginResult.Error(e.message ?: "Apple sign in failed"))
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

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}