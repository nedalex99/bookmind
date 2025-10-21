package com.nedalex.bookmind.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookRecommendation(
    @SerialName("book")
    val book: Book,

    @SerialName("score")
    val score: Double,

    @SerialName("reason")
    val reason: String, // Why this book was recommended
)

@Serializable
data class RecommendationRequest(
    @SerialName("user_id")
    val userId: String,

    @SerialName("genres")
    val genres: List<String> = emptyList(),

    @SerialName("authors")
    val authors: List<String> = emptyList(),

    @SerialName("books_read")
    val booksRead: List<String> = emptyList(), // Book IDs

    @SerialName("limit")
    val limit: Int = 10,
)