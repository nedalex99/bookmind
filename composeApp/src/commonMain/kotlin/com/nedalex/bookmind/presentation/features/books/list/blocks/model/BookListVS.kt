package com.nedalex.bookmind.presentation.features.books.list.blocks.model

import com.nedalex.bookmind.data.models.Book

data class BookListVS(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
)