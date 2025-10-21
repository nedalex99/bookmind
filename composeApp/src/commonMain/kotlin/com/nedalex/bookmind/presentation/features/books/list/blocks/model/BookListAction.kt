package com.nedalex.bookmind.presentation.features.books.list.blocks.model

sealed interface BookListAction {
    data object LoadBooks : BookListAction
    data object RefreshBooks : BookListAction
    data class SearchBooks(val query: String) : BookListAction
    data class BookClicked(val bookId: String) : BookListAction
    data object AddBookClicked : BookListAction
    data object RecommendationsClicked : BookListAction
    data object MyLibraryClicked : BookListAction
}