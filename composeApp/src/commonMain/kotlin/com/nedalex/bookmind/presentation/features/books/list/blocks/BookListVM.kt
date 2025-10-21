package com.nedalex.bookmind.presentation.features.books.list.blocks

import androidx.lifecycle.viewModelScope
import com.nedalex.bookmind.architecture.blocks.BaseVM
import com.nedalex.bookmind.domain.book.BookRepository
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListAction
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListNavigation
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListResult
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListVS
import kotlinx.coroutines.launch

class BookListVM(
    private val bookRepository: BookRepository
) : BaseVM<BookListVS, BookListResult, BookListAction, BookListNavigation>(
    viewState = BookListVS(),
    reducer = BookListRR()
) {

    init {
        loadBooks()
    }

    override fun onAction(action: BookListAction) {
        when (action) {
            BookListAction.LoadBooks -> loadBooks()
            BookListAction.RefreshBooks -> refreshBooks()
            is BookListAction.SearchBooks -> searchBooks(action.query)
            is BookListAction.BookClicked -> navigate(BookListNavigation.ToBookDetail(action.bookId))
            BookListAction.AddBookClicked -> navigate(BookListNavigation.ToAddBook)
            BookListAction.RecommendationsClicked -> navigate(BookListNavigation.ToRecommendations)
            BookListAction.MyLibraryClicked -> navigate(BookListNavigation.ToMyLibrary)
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            onResult(BookListResult.ShowLoading)

            bookRepository.getAllBooks()
                .onSuccess { books ->
                    onResult(BookListResult.BooksLoaded(books))
                }
                .onFailure { error ->
                    onResult(BookListResult.Error(error.message ?: "Failed to load books"))
                }
        }
    }

    private fun refreshBooks() {
        loadBooks()
    }

    private fun searchBooks(query: String) {
        onResult(BookListResult.SearchQueryChanged(query))

        if (query.isBlank()) {
            loadBooks()
            return
        }

        viewModelScope.launch {
            onResult(BookListResult.ShowLoading)

            bookRepository.searchBooks(query)
                .onSuccess { books ->
                    onResult(BookListResult.BooksLoaded(books))
                }
                .onFailure { error ->
                    onResult(BookListResult.Error(error.message ?: "Search failed"))
                }
        }
    }
}