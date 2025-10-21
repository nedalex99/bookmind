package com.nedalex.bookmind.presentation.features.books.list.blocks.model

sealed interface BookListNavigation {
    data class ToBookDetail(val bookId: String) : BookListNavigation
    data object ToAddBook : BookListNavigation
    data object ToRecommendations : BookListNavigation
    data object ToMyLibrary : BookListNavigation
}