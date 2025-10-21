package com.nedalex.bookmind.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreference(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("favorite_genres")
    val favoriteGenres: List<String> = emptyList(),

    @SerialName("favorite_authors")
    val favoriteAuthors: List<String> = emptyList(),

    @SerialName("disliked_genres")
    val dislikedGenres: List<String> = emptyList(),

    @SerialName("updated_at")
    val updatedAt: String,
)