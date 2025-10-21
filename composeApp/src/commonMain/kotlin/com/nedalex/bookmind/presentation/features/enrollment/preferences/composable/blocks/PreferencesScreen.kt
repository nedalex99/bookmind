package com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nedalex.bookmind.app.Route
import com.nedalex.bookmind.architecture.blocks.navigation.NavigationHandler
import com.nedalex.bookmind.domain.preference.AuthorEntity
import com.nedalex.bookmind.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PreferencesScreen(
    vm: PreferencesVM,
    navController: NavController
) {
    fun act(action: PreferencesAction) = vm.onAction(action)

    NavigationHandler(
        navController = navController,
        navigationFlow = vm.navigation,
        onNavigate = { event ->
            when (event) {
                PreferencesNavigation.Back -> {
                    navController.popBackStack()
                }

                PreferencesNavigation.ToHome -> {
                    // Navigate to home and clear backstack
                    navController.navigate(Route.MainGraph) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    )

    Screen(vm.viewState, ::act)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    vs: PreferencesVS,
    act: (PreferencesAction) -> Unit
) {
    ReadingAppTheme {
        Scaffold(
            topBar = {
                if (vs.currentStep != PreferencesStep.GENRES) {
                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = { act(PreferencesAction.BackClicked) }) {
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
            },
            bottomBar = {
                BottomNavigationBar(
                    currentStep = vs.currentStep,
                    canProceed = canProceedToNext(vs),
                    isSaving = vs.isSaving,
                    onSkip = { act(PreferencesAction.SkipStep) },
                    onNext = { act(PreferencesAction.NextStep) }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header
                HeaderSection(vs.currentStep)

                // Progress Bar
                ProgressBar(vs.currentStep)

                // Content
                when (vs.currentStep) {
                    PreferencesStep.GENRES -> GenresStep(vs, act)
                    PreferencesStep.AUTHORS -> AuthorsStep(vs, act)
                    PreferencesStep.BOOKS -> BooksStep(vs, act)
                    PreferencesStep.GOAL -> GoalStep(vs, act)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(currentStep: PreferencesStep) {
    val (stepText, title, subtitle) = when (currentStep) {
        PreferencesStep.GENRES -> Triple(
            "STEP 1 OF 4",
            "Select Your Favorite Genres",
            "Choose at least 3 genres to get personalized recommendations"
        )

        PreferencesStep.AUTHORS -> Triple(
            "STEP 2 OF 4",
            "Add Your Favorite Authors",
            "Help us understand your reading preferences"
        )

        PreferencesStep.BOOKS -> Triple(
            "STEP 3 OF 4",
            "Books You've Read",
            "Select books you've enjoyed to refine recommendations"
        )

        PreferencesStep.GOAL -> Triple(
            "STEP 4 OF 4",
            "Set Your Reading Goal",
            "Challenge yourself and track your progress"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stepText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ProgressBar(currentStep: PreferencesStep) {
    val progress = when (currentStep) {
        PreferencesStep.GENRES -> 0.25f
        PreferencesStep.AUTHORS -> 0.5f
        PreferencesStep.BOOKS -> 0.75f
        PreferencesStep.GOAL -> 1f
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(3.dp),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
private fun BottomNavigationBar(
    currentStep: PreferencesStep,
    canProceed: Boolean,
    isSaving: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Only show Skip button for optional steps
            if (currentStep != PreferencesStep.GENRES) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                ) {
                    Text("Skip")
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(if (currentStep == PreferencesStep.GENRES) 1f else 1f),
                enabled = canProceed && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = when (currentStep) {
                            PreferencesStep.GENRES -> "Continue"
                            PreferencesStep.GOAL -> "Get Started"
                            else -> "Continue"
                        }
                    )
                }
            }
        }
    }
}

private fun canProceedToNext(vs: PreferencesVS): Boolean {
    return when (vs.currentStep) {
        PreferencesStep.GENRES -> vs.selectedGenres.size >= 3
        PreferencesStep.AUTHORS -> true // Optional
        PreferencesStep.BOOKS -> true // Optional
        PreferencesStep.GOAL -> true
    }
}

// ============================================
// PREVIEWS
// ============================================

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenGenresPreview() {
    ReadingAppTheme {
        Screen(
            vs = PreferencesVS(
                currentStep = PreferencesStep.GENRES,
                selectedGenres = setOf("fiction", "mystery", "scifi")
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenAuthorsPreview() {
    ReadingAppTheme {
        Screen(
            vs = PreferencesVS(
                currentStep = PreferencesStep.AUTHORS,
                selectedAuthors = setOf(
                    AuthorEntity("1", "Jane Austen", "Pride and Prejudice, Emma")
                )
            ),
            act = {}
        )
    }
}