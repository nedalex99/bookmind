package com.nedalex.bookmind.presentation.features.books.list.blocks

import com.nedalex.bookmind.architecture.blocks.reducer.BaseReducer
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListResult
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListVS

class BookListRR : BaseReducer<BookListVS, BookListResult> {
    override fun reduce(viewState: BookListVS, result: BookListResult): BookListVS {
        return when (result) {
            BookListResult.ShowLoading -> viewState.copy(isLoading = true, error = null)
            BookListResult.HideLoading -> viewState.copy(isLoading = false)
            is BookListResult.BooksLoaded -> viewState.copy(
                books = result.books,
                isLoading = false,
                error = null
            )
            is BookListResult.Error -> viewState.copy(
                isLoading = false,
                error = result.message
            )
            is BookListResult.SearchQueryChanged -> viewState.copy(
                searchQuery = result.query
            )
        }
    }
}