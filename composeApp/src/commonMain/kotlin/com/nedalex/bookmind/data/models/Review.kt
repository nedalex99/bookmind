package com.nedalex.bookmind.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    @SerialName("id")
    val id: String,

    @SerialName("book_id")
    val bookId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("user_name")
    val userName: String? = null,

    @SerialName("rating")
    val rating: Int, // 1-5 stars

    @SerialName("review_text")
    val reviewText: String? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String? = null,
)