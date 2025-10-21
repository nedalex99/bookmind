package com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks

import com.nedalex.bookmind.architecture.blocks.reducer.BaseReducer

class PreferencesRR : BaseReducer<PreferencesVS, PreferencesResult> {
    override fun reduce(
        viewState: PreferencesVS,
        result: PreferencesResult
    ): PreferencesVS {
        return when (result) {
            is PreferencesResult.Loading -> viewState.copy(
                isLoading = true,
                error = null
            )

            is PreferencesResult.Saving -> viewState.copy(
                isSaving = true,
                error = null
            )

            is PreferencesResult.SaveSuccess -> viewState.copy(
                isSaving = false,
                error = null
            )

            is PreferencesResult.Error -> viewState.copy(
                isLoading = false,
                isSaving = false,
                error = result.message
            )

            // Navigation
            is PreferencesResult.StepChanged -> viewState.copy(
                currentStep = result.step,
                error = null
            )

            // Genres
            is PreferencesResult.GenreToggled -> {
                val newGenres = if (viewState.selectedGenres.contains(result.genre)) {
                    viewState.selectedGenres - result.genre
                } else {
                    viewState.selectedGenres + result.genre
                }
                viewState.copy(selectedGenres = newGenres)
            }

            // Authors
            is PreferencesResult.AuthorSearchChanged -> viewState.copy(
                authorSearchQuery = result.query
            )

            is PreferencesResult.AuthorSuggestionsLoaded -> viewState.copy(
                authorSuggestions = result.authors,
                isLoading = false
            )

            is PreferencesResult.AuthorSelected -> {
                val newAuthors = if (!viewState.selectedAuthors.any { it.id == result.author.id }) {
                    viewState.selectedAuthors + result.author
                } else {
                    viewState.selectedAuthors
                }
                viewState.copy(
                    selectedAuthors = newAuthors,
                    authorSuggestions = viewState.authorSuggestions.filter { it.id != result.author.id }
                )
            }

            is PreferencesResult.AuthorRemoved -> viewState.copy(
                selectedAuthors = viewState.selectedAuthors.filter { it.id != result.authorId }.toSet()
            )

            // Books
            is PreferencesResult.BookSearchChanged -> viewState.copy(
                bookSearchQuery = result.query
            )

            is PreferencesResult.BookSuggestionsLoaded -> viewState.copy(
                bookSuggestions = result.books,
                isLoading = false
            )

            is PreferencesResult.BookToggled -> {
                val newBooks = if (viewState.selectedBooks.any { it.id == result.book.id }) {
                    viewState.selectedBooks.filter { it.id != result.book.id }
                } else {
                    viewState.selectedBooks + result.book
                }
                viewState.copy(selectedBooks = newBooks.toSet())
            }

            // Goal
            is PreferencesResult.GoalChanged -> viewState.copy(
                readingGoal = result.goal
            )

            is PreferencesResult.AuthorPageLoaded -> {
                val updatedAuthors = if (result.isInitialLoad) {
                    result.authors // Replace
                } else {
                    viewState.authorSuggestions + result.authors // Append
                }

                viewState.copy(
                    authorSuggestions = updatedAuthors,
                    authorOffset = result.nextOffset,
                    hasMoreAuthors = result.hasMore,
                    currentAuthorTier = result.tier,
                    isLoadingMoreAuthors = false,
                    isLoading = false
                )
            }

            is PreferencesResult.BookPageLoaded -> {
                val updatedBooks = if (result.isInitialLoad) {
                    result.books
                } else {
                    viewState.bookSuggestions + result.books
                }

                viewState.copy(
                    bookSuggestions = updatedBooks,
                    bookOffset = result.nextOffset,
                    hasMoreBooks = result.hasMore,
                    currentBookTier = result.tier,
                    isLoadingMoreBooks = false,
                    isLoading = false
                )
            }

            PreferencesResult.LoadingMoreAuthors -> viewState.copy(
                isLoadingMoreAuthors = true
            )

            PreferencesResult.LoadingMoreBooks -> viewState.copy(
                isLoadingMoreBooks = true
            )
        }
    }
}