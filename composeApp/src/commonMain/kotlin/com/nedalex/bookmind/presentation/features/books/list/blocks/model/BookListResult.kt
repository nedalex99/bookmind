package com.nedalex.bookmind.presentation.features.books.list.blocks.model

import com.nedalex.bookmind.data.models.Book

sealed interface BookListResult {
    data object ShowLoading : BookListResult
    data object HideLoading : BookListResult
    data class BooksLoaded(val books: List<Book>) : BookListResult
    data class Error(val message: String) : BookListResult
    data class SearchQueryChanged(val query: String) : BookListResult
}