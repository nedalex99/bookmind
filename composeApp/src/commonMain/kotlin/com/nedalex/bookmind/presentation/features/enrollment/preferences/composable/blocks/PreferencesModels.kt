package com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks

import com.nedalex.bookmind.domain.preference.AuthorEntity
import com.nedalex.bookmind.domain.preference.BookEntity
import com.nedalex.bookmind.domain.preference.RecommendationTier

// ============================================
// VIEW STATE
// ============================================

data class PreferencesVS(
    val currentStep: PreferencesStep = PreferencesStep.GENRES,
    val selectedGenres: Set<String> = emptySet(),
    val selectedAuthors: Set<AuthorEntity> = emptySet(),
    val selectedBooks: Set<BookEntity> = emptySet(),
    val readingGoal: Int = 24,

    // Author recommendations with infinite scroll
    val authorSuggestions: List<AuthorEntity> = emptyList(),
    val authorOffset: Int = 0,
    val hasMoreAuthors: Boolean = true,
    val isLoadingMoreAuthors: Boolean = false,
    val currentAuthorTier: RecommendationTier? = null,

    // Book recommendations with infinite scroll
    val bookSuggestions: List<BookEntity> = emptyList(),
    val bookOffset: Int = 0,
    val hasMoreBooks: Boolean = true,
    val isLoadingMoreBooks: Boolean = false,
    val currentBookTier: RecommendationTier? = null,

    val authorSearchQuery: String = "",
    val bookSearchQuery: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

enum class PreferencesStep {
    GENRES,
    AUTHORS,
    BOOKS,
    GOAL
}

// ============================================
// ACTIONS
// ============================================

sealed interface PreferencesAction {
    // Navigation
    data object NextStep : PreferencesAction
    data object PreviousStep : PreferencesAction
    data object SkipStep : PreferencesAction
    data object BackClicked : PreferencesAction
    data object CompleteSetup : PreferencesAction

    // Genre Actions
    data class GenreToggled(val genre: String) : PreferencesAction

    // Author Actions
    data class AuthorSearchChanged(val query: String) : PreferencesAction
    data class AuthorSelected(val author: AuthorEntity) : PreferencesAction
    data class AuthorRemoved(val authorId: String) : PreferencesAction

    // Book Actions
    data class BookSearchChanged(val query: String) : PreferencesAction
    data class BookToggled(val book: BookEntity) : PreferencesAction

    // Goal Actions
    data class GoalChanged(val goal: Int) : PreferencesAction

    data object LoadMoreAuthors : PreferencesAction
    data object LoadMoreBooks : PreferencesAction
}

// ============================================
// RESULTS
// ============================================

sealed interface PreferencesResult {
    // Navigation
    data class StepChanged(val step: PreferencesStep) : PreferencesResult

    // Genre Results
    data class GenreToggled(val genre: String) : PreferencesResult

    // Author Results
    data class AuthorSearchChanged(val query: String) : PreferencesResult
    data class AuthorSuggestionsLoaded(val authors: List<AuthorEntity>) : PreferencesResult
    data class AuthorSelected(val author: AuthorEntity) : PreferencesResult
    data class AuthorRemoved(val authorId: String) : PreferencesResult

    // Book Results
    data class BookSearchChanged(val query: String) : PreferencesResult
    data class BookSuggestionsLoaded(val books: List<BookEntity>) : PreferencesResult
    data class BookToggled(val book: BookEntity) : PreferencesResult

    // Goal Results
    data class GoalChanged(val goal: Int) : PreferencesResult

    // UI State
    data object Loading : PreferencesResult
    data object Saving : PreferencesResult
    data object SaveSuccess : PreferencesResult
    data class Error(val message: String) : PreferencesResult

    data class AuthorPageLoaded(
        val authors: List<AuthorEntity>,
        val hasMore: Boolean,
        val nextOffset: Int,
        val tier: RecommendationTier,
        val isInitialLoad: Boolean = false
    ) : PreferencesResult

    data class BookPageLoaded(
        val books: List<BookEntity>,
        val hasMore: Boolean,
        val nextOffset: Int,
        val tier: RecommendationTier,
        val isInitialLoad: Boolean = false
    ) : PreferencesResult

    object LoadingMoreAuthors : PreferencesResult
    object LoadingMoreBooks : PreferencesResult
}

// ============================================
// NAVIGATION EVENTS
// ============================================

sealed interface PreferencesNavigation {
    data object ToHome : PreferencesNavigation
    data object Back : PreferencesNavigation
}

// ============================================
// AVAILABLE GENRES
// ============================================

object AvailableGenres {
    val all = listOf(
        Genre("fiction", "Fiction", "📖"),
        Genre("mystery", "Mystery", "🔍"),
        Genre("romance", "Romance", "💕"),
        Genre("scifi", "Sci-Fi", "🚀"),
        Genre("fantasy", "Fantasy", "🐉"),
        Genre("thriller", "Thriller", "😱"),
        Genre("historical", "Historical", "📜"),
        Genre("biography", "Biography", "👤"),
        Genre("selfhelp", "Self-Help", "💡"),
        Genre("business", "Business", "💼"),
        Genre("horror", "Horror", "👻"),
        Genre("poetry", "Poetry", "✍️")
    )
}

data class Genre(
    val id: String,
    val name: String,
    val emoji: String
)