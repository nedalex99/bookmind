package com.nedalex.bookmind.domain.recommendation

import kotlinx.serialization.Serializable

interface AIRecommendationService {
    suspend fun getRecommendations(
        genres: List<String>,
        authorIds: List<String>,
        booksRead: List<String>,
        limit: Int = 10
    ): List<BookRecommendation>
}

@Serializable
data class BookRecommendation(
    val title: String,
    val author: String,
    val genre: String,
    val rating: Double,
    val matchPercentage: Int,
    val reason: String,
    val pageCount: Int,
    val publicationYear: Int,
    val description: String,
    val similarTo: List<String> = emptyList()
)