package com.nedalex.presentation.features.enrollment.resetpassword.compose

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.nedalex.core.Route
import com.nedalex.presentation.architecture.navigation.NavigationHandler
import com.nedalex.presentation.compose.ReadingPrimaryButton
import com.nedalex.presentation.compose.ReadingTextField
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.ResetPasswordVM
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordAction
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordNavigation
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.model.ResetPasswordVS
import com.nedalex.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResetPasswordScreen(
    vm: ResetPasswordVM,
    navController: NavController
) {
    fun act(action: ResetPasswordAction) = vm.onAction(action)

    NavigationHandler(
        navController = navController,
        navigationFlow = vm.navigation,
        onNavigate = { event ->
            when (event) {
                ResetPasswordNavigation.ToLogin -> navController.popBackStack()
            }
        }
    )

    Screen(vm.viewState, ::act)
}

@Composable
private fun Screen(
    vs: ResetPasswordVS,
    act: (ResetPasswordAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { act(ResetPasswordAction.BackToLoginClicked) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to login",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Logo/Brand Section
            LogoSection()
            Spacer(modifier = Modifier.height(32.dp))
            // Form Section
            FormSection(vs, act)
        }
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

        // Title Text
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                fontSize = 32.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your email and we'll send you a link to reset your password",
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
    vs: ResetPasswordVS,
    act: (ResetPasswordAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Message
        if (vs.isSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Password reset email sent! Please check your inbox.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Email Field
        ReadingTextField(
            value = vs.email,
            onValueChange = { act(ResetPasswordAction.EmailChanged(it)) },
            label = "Email",
            placeholder = "your@email.com",
            isError = vs.emailError != null,
            errorMessage = vs.emailError,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

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

        // Reset Button
        ReadingPrimaryButton(
            onClick = { act(ResetPasswordAction.ResetButtonClicked) },
            text = "Send Reset Link",
            isLoading = vs.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Back to Login Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Remember your password? ",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.clickable {
                    act(ResetPasswordAction.BackToLoginClicked)
                }
            )
        }
    }
}

@Composable
@Preview
private fun ResetPasswordScreenPreview() = ReadingAppTheme {
    Screen(
        vs = ResetPasswordVS(),
        act = {}
    )
}

@Composable
@Preview
private fun ResetPasswordScreenSuccessPreview() = ReadingAppTheme {
    Screen(
        vs = ResetPasswordVS(
            email = "test@example.com",
            isSuccess = true
        ),
        act = {}
    )
}