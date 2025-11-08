package com.nedalex.presentation.features.enrollment.signin.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nedalex.core.Platform
import com.nedalex.core.Route
import com.nedalex.presentation.architecture.navigation.NavigationHandler
import com.nedalex.presentation.compose.ReadingPrimaryButton
import com.nedalex.presentation.compose.ReadingSocialButton
import com.nedalex.presentation.compose.ReadingTextButton
import com.nedalex.presentation.compose.ReadingTextField
import com.nedalex.presentation.features.enrollment.signin.blocks.LoginVM
import com.nedalex.presentation.features.enrollment.signin.blocks.model.LoginAction
import com.nedalex.presentation.features.enrollment.signin.blocks.model.LoginNavigation
import com.nedalex.presentation.features.enrollment.signin.blocks.model.LoginVS
import com.nedalex.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    vm: LoginVM,
    navController: NavController
) {
    fun act(action: LoginAction) = vm.onAction(action)

    NavigationHandler(
        navController = navController,
        navigationFlow = vm.navigation,
        onNavigate = { event ->
            when (event) {
                LoginNavigation.ToSignUp -> navController.navigate(Route.SignUp)
                LoginNavigation.ToResetPassword -> navController.navigate(Route.ResetPassword)
                LoginNavigation.ToHome -> {
                    println("Authenticated")
//                    navController.navigate(Route.Dashboard) {
//                        popUpTo(0) { inclusive = true }
//                    }
                }
                LoginNavigation.ToPreferences -> navController.navigate(Route.Preferences)
            }
        }
    )

    Screen(vm.viewState, ::act)
}

@Composable
private fun Screen(
    vs: LoginVS,
    act: (LoginAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Logo/Brand Section
        LogoSection()

        // Form Section
        FormSection(vs, act)
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo Box
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Welcome Text
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                fontSize = 32.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue your reading journey",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FormSection(
    vs: LoginVS,
    act: (LoginAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email Field
        ReadingTextField(
            value = vs.email,
            onValueChange = { act(LoginAction.EmailChanged(it)) },
            label = "Email",
            placeholder = "your@email.com",
            isError = vs.emailError != null,
            errorMessage = vs.emailError,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        ReadingTextField(
            value = vs.password,
            onValueChange = { act(LoginAction.PasswordChanged(it)) },
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            isError = vs.passwordError != null,
            errorMessage = vs.passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        // Forgot Password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            ReadingTextButton(
                onClick = { act(LoginAction.ForgotPasswordClicked) },
                text = "Forgot Password?"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        if (vs.error != null) {
            Text(
                text = vs.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Sign In Button
        ReadingPrimaryButton(
            onClick = { act(LoginAction.SignInButtonClicked) },
            text = "Sign In",
            isLoading = vs.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        DividerWithText(text = "OR CONTINUE WITH")

        Spacer(modifier = Modifier.height(24.dp))

        // Social Sign In Buttons
        ReadingSocialButton(
            onClick = { act(LoginAction.GoogleSignInClicked) },
            text = "Continue with Google",
            icon = {
                // Add Google icon here
                Text(
                    text = "G",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (vs.platform == Platform.iOS) {
            ReadingSocialButton(
                onClick = { act(LoginAction.AppleSignInClicked) },
                text = "Continue with Apple",
                icon = {
                    // Add Apple icon here
                    Text(
                        text = "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable {
                    act(LoginAction.SignUpButtonClicked)
                }
            )
        }
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
private fun LoginScreenPreview() = ReadingAppTheme {
    Screen(
        vs = LoginVS(platform = Platform.iOS),
        act = {}
    )
}