package com.nedalex.presentation.features.enrollment.singup.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nedalex.core.Platform
import com.nedalex.core.Route
import com.nedalex.presentation.architecture.navigation.NavigationHandler
import com.nedalex.presentation.compose.ReadingPrimaryButton
import com.nedalex.presentation.compose.ReadingSocialButton
import com.nedalex.presentation.compose.ReadingTextField
import com.nedalex.presentation.features.enrollment.singup.blocks.SignUpVM
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpAction
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpNavigation
import com.nedalex.presentation.features.enrollment.singup.blocks.model.SignUpVS
import com.nedalex.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    vm: SignUpVM,
    navController: NavController
) {
    fun act(action: SignUpAction) = vm.onAction(action)

    NavigationHandler(
        navController = navController,
        navigationFlow = vm.navigation,
        onNavigate = { event ->
            when (event) {
                SignUpNavigation.ToSignIn -> {
                    navController.popBackStack()
                }
                SignUpNavigation.ToPreferences -> {
                    navController.navigate(Route.Preferences) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    )

    Screen(vm.viewState, ::act, onBackClick = { navController.popBackStack() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    vs: SignUpVS,
    act: (SignUpAction) -> Unit,
    onBackClick: () -> Unit = {}
) {
    ReadingAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                HeaderSection()

                Spacer(modifier = Modifier.height(40.dp))

                // Form Section
                FormSection(vs, act)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Light,
                fontSize = 32.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join our community of book lovers",
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
    vs: SignUpVS,
    act: (SignUpAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name Field
        ReadingTextField(
            value = vs.name,
            onValueChange = { act(SignUpAction.NameChanged(it)) },
            label = "Full Name",
            placeholder = "John Doe",
            isError = vs.nameError != null,
            errorMessage = vs.nameError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        ReadingTextField(
            value = vs.email,
            onValueChange = { act(SignUpAction.EmailChanged(it)) },
            label = "Email",
            placeholder = "your@email.com",
            isError = vs.emailError != null,
            errorMessage = vs.emailError,
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        ReadingTextField(
            value = vs.password,
            onValueChange = { act(SignUpAction.PasswordChanged(it)) },
            label = "Password",
            placeholder = "Create a password",
            isPassword = true,
            isError = vs.passwordError != null,
            errorMessage = vs.passwordError,
            modifier = Modifier.fillMaxWidth()
        )

        // Password hint
        Text(
            text = "Must be at least 6 characters",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        ReadingTextField(
            value = vs.confirmPassword,
            onValueChange = { act(SignUpAction.ConfirmPasswordChanged(it)) },
            label = "Confirm Password",
            placeholder = "Confirm your password",
            isPassword = true,
            isError = vs.confirmPasswordError != null,
            errorMessage = vs.confirmPasswordError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms & Conditions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = vs.acceptedTerms,
                onCheckedChange = { act(SignUpAction.TermsAcceptedChanged(it)) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "I agree to the Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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

        // Sign Up Button
        ReadingPrimaryButton(
            onClick = { act(SignUpAction.SignUpButtonClicked) },
            text = "Create Account",
            isLoading = vs.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        DividerWithText(text = "OR CONTINUE WITH")

        Spacer(modifier = Modifier.height(24.dp))

        // Social Sign Up Buttons
        ReadingSocialButton(
            onClick = { act(SignUpAction.GoogleSignUpClicked) },
            text = "Continue with Google",
            icon = {
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
                onClick = { act(SignUpAction.AppleSignUpClicked) },
                text = "Continue with Apple",
                icon = {
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

        // Sign In Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
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
                    act(SignUpAction.SignInButtonClicked)
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

// ============================================
// PREVIEWS
// ============================================

@Preview(showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    ReadingAppTheme {
        Screen(
            vs = SignUpVS(
                name = "John Doe",
                email = "john@example.com",
                platform = Platform.iOS
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenWithErrorPreview() {
    ReadingAppTheme {
        Screen(
            vs = SignUpVS(
                name = "J",
                email = "invalid-email",
                password = "123",
                confirmPassword = "456",
                nameError = "Name must be at least 2 characters",
                emailError = "Invalid email format",
                passwordError = "Password must be at least 6 characters",
                confirmPasswordError = "Passwords do not match",
                platform = Platform.Android
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpScreenLoadingPreview() {
    ReadingAppTheme {
        Screen(
            vs = SignUpVS(
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                confirmPassword = "password123",
                acceptedTerms = true,
                isLoading = true,
                platform = Platform.iOS
            ),
            act = {}
        )
    }
}