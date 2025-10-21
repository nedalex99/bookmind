package com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.bookmind.architecture.blocks.BaseVM
import com.nedalex.bookmind.domain.preference.PreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PreferencesVM(
    private val preferencesRepository: PreferencesRepository
) : BaseVM<PreferencesVS, PreferencesResult, PreferencesAction, PreferencesNavigation>(
    viewState = PreferencesVS(),
    reducer = PreferencesRR()
) {

    private var authorSearchJob: Job? = null
    private var bookSearchJob: Job? = null

    init {
        loadInitialSuggestions()
    }

    override fun onAction(action: PreferencesAction) {
        when (action) {
            // Navigation
            PreferencesAction.NextStep -> handleNextStep()
            PreferencesAction.PreviousStep -> handlePreviousStep()
            PreferencesAction.SkipStep -> handleNextStep()
            PreferencesAction.BackClicked -> handleBackClicked()
            PreferencesAction.CompleteSetup -> completeSetup()

            // Genres
            is PreferencesAction.GenreToggled -> {
                onResult(PreferencesResult.GenreToggled(action.genre))
            }

            // Authors
            is PreferencesAction.AuthorSearchChanged -> {
                onResult(PreferencesResult.AuthorSearchChanged(action.query))

                authorSearchJob?.cancel()
                authorSearchJob = viewModelScope.launch {
                    delay(500)
                    if (action.query.isNotBlank() && action.query.length >= 2) {
                        searchAuthors(action.query)
                    } else if (action.query.isBlank()) {
                        // Reload smart recommendations based on genres
                        loadSmartAuthorRecommendations()
                    }
                }
            }

            is PreferencesAction.AuthorSelected -> {
                viewModelScope.launch {
                    try {
                        if (!action.author.isFromDatabase) {
                            preferencesRepository.saveSelectedAuthor(action.author)
                        }
                        onResult(PreferencesResult.AuthorSelected(action.author))
                    } catch (e: Exception) {
                        onResult(PreferencesResult.Error("Failed to save author: ${e.message}"))
                    }
                }
            }

            is PreferencesAction.AuthorRemoved -> {
                onResult(PreferencesResult.AuthorRemoved(action.authorId))
            }

            // Books
            is PreferencesAction.BookSearchChanged -> {
                onResult(PreferencesResult.BookSearchChanged(action.query))

                bookSearchJob?.cancel()
                bookSearchJob = viewModelScope.launch {
                    delay(500)
                    if (action.query.isNotBlank() && action.query.length >= 2) {
                        searchBooks(action.query)
                    } else if (action.query.isBlank()) {
                        // Reload smart recommendations based on authors and genres
                        loadSmartBookRecommendations()
                    }
                }
            }

            is PreferencesAction.BookToggled -> {
                viewModelScope.launch {
                    try {
                        if (!action.book.isFromDatabase) {
                            preferencesRepository.saveSelectedBook(action.book)
                        }
                        onResult(PreferencesResult.BookToggled(action.book))
                    } catch (e: Exception) {
                        onResult(PreferencesResult.Error("Failed to save book: ${e.message}"))
                    }
                }
            }

            // Goal
            is PreferencesAction.GoalChanged -> {
                onResult(PreferencesResult.GoalChanged(action.goal))
            }

            PreferencesAction.LoadMoreAuthors -> {
                if (viewState.hasMoreAuthors && !viewState.isLoadingMoreAuthors) {
                    loadMoreAuthors()
                }
            }

            PreferencesAction.LoadMoreBooks -> {
                if (viewState.hasMoreBooks && !viewState.isLoadingMoreBooks) {
                    loadMoreBooks()
                }
            }
        }
    }

    private fun handleNextStep() {
        val currentStep = viewState.currentStep
        val nextStep = when (currentStep) {
            PreferencesStep.GENRES -> {
                if (viewState.selectedGenres.size < 3) {
                    onResult(PreferencesResult.Error("Please select at least 3 genres"))
                    return
                }
                // Load smart author recommendations based on selected genres
                loadSmartAuthorRecommendations()
                PreferencesStep.AUTHORS
            }

            PreferencesStep.AUTHORS -> {
                // Load smart book recommendations based on selected authors and genres
                loadSmartBookRecommendations()
                PreferencesStep.BOOKS
            }

            PreferencesStep.BOOKS -> PreferencesStep.GOAL
            PreferencesStep.GOAL -> {
                completeSetup()
                return
            }
        }
        onResult(PreferencesResult.StepChanged(nextStep))
    }

    private fun handlePreviousStep() {
        val currentStep = viewState.currentStep
        val previousStep = when (currentStep) {
            PreferencesStep.GENRES -> {
                navigate(PreferencesNavigation.Back)
                return
            }

            PreferencesStep.AUTHORS -> PreferencesStep.GENRES
            PreferencesStep.BOOKS -> PreferencesStep.AUTHORS
            PreferencesStep.GOAL -> PreferencesStep.BOOKS
        }
        onResult(PreferencesResult.StepChanged(previousStep))
    }

    private fun handleBackClicked() {
        if (viewState.currentStep == PreferencesStep.GENRES) {
            navigate(PreferencesNavigation.Back)
        } else {
            handlePreviousStep()
        }
    }

    private fun loadInitialSuggestions() {
        viewModelScope.launch {
            try {
                // Load popular authors (no genre context yet)
                loadPopularAuthors()

                // Load popular books (no context yet)
                loadPopularBooks()
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load suggestions"))
            }
        }
    }

    // ============================================
    // Smart Recommendations (Hybrid Approach)
    // ============================================

    private fun loadSmartAuthorRecommendations() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val result = preferencesRepository.getSmartAuthorRecommendations(
                    genres = viewState.selectedGenres.toList(),
                    offset = 0,
                    limit = 10,
                    useAI = true
                )
                onResult(
                    PreferencesResult.AuthorPageLoaded(
                        authors = result.items,
                        hasMore = result.hasMore,
                        nextOffset = result.nextOffset,
                        tier = result.tier,
                        isInitialLoad = true
                    )
                )
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load authors"))
            }
        }
    }

    private fun loadMoreAuthors() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.LoadingMoreAuthors)
                val result = preferencesRepository.getSmartAuthorRecommendations(
                    genres = viewState.selectedGenres.toList(),
                    offset = viewState.authorOffset,
                    limit = 10,
                    useAI = false // AI already loaded in initial load
                )
                onResult(PreferencesResult.AuthorPageLoaded(
                    authors = result.items,
                    hasMore = result.hasMore,
                    nextOffset = result.nextOffset,
                    tier = result.tier,
                    isInitialLoad = false
                ))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load more authors"))
            }
        }
    }

    private fun loadSmartBookRecommendations() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val result = preferencesRepository.getSmartBookRecommendations(
                    genres = viewState.selectedGenres.toList(),
                    authorIds = viewState.selectedAuthors.map { it.id },
                    offset = 0,
                    limit = 10,
                    useAI = true
                )
                onResult(PreferencesResult.BookPageLoaded(
                    books = result.items,
                    hasMore = result.hasMore,
                    nextOffset = result.nextOffset,
                    tier = result.tier,
                    isInitialLoad = true
                ))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load books"))
            }
        }
    }

    private fun loadMoreBooks() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.LoadingMoreBooks)
                val result = preferencesRepository.getSmartBookRecommendations(
                    genres = viewState.selectedGenres.toList(),
                    authorIds = viewState.selectedAuthors.map { it.id },
                    offset = viewState.bookOffset,
                    limit = 10,
                    useAI = false
                )
                onResult(PreferencesResult.BookPageLoaded(
                    books = result.items,
                    hasMore = result.hasMore,
                    nextOffset = result.nextOffset,
                    tier = result.tier,
                    isInitialLoad = false
                ))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load more books"))
            }
        }
    }

    // ============================================
    // Fallback Methods (for initial load and search)
    // ============================================

    private fun loadPopularAuthors() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val authors = preferencesRepository.getPopularAuthors(limit = 20)
                onResult(PreferencesResult.AuthorSuggestionsLoaded(authors))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load authors"))
            }
        }
    }

    private fun loadPopularBooks() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val books = preferencesRepository.getPopularBooks(limit = 20)
                onResult(PreferencesResult.BookSuggestionsLoaded(books))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to load books"))
            }
        }
    }

    private fun searchAuthors(query: String) {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val authors = preferencesRepository.searchAuthors(query, limit = 10)
                onResult(PreferencesResult.AuthorSuggestionsLoaded(authors))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to search authors"))
            }
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Loading)
                val books = preferencesRepository.searchBooks(query, limit = 20)
                onResult(PreferencesResult.BookSuggestionsLoaded(books))
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to search books"))
            }
        }
    }

    private fun completeSetup() {
        viewModelScope.launch {
            try {
                onResult(PreferencesResult.Saving)

                preferencesRepository.saveUserPreferences(
                    genres = viewState.selectedGenres.toList(),
                    authorIds = viewState.selectedAuthors.map { it.id },
                    bookIds = viewState.selectedBooks.map { it.id },
                    readingGoal = viewState.readingGoal
                )

                onResult(PreferencesResult.SaveSuccess)
                navigate(PreferencesNavigation.ToHome)
            } catch (e: Exception) {
                onResult(PreferencesResult.Error(e.message ?: "Failed to save preferences"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authorSearchJob?.cancel()
        bookSearchJob?.cancel()
    }
}