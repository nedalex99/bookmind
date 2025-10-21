package com.nedalex.bookmind.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ReadingStatus {
    @SerialName("want_to_read")
    WANT_TO_READ,

    @SerialName("currently_reading")
    CURRENTLY_READING,

    @SerialName("finished")
    FINISHED,
}

@Serializable
data class UserBook(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("book_id")
    val bookId: String,

    @SerialName("status")
    val status: ReadingStatus,

    @SerialName("started_reading_at")
    val startedReadingAt: String? = null,

    @SerialName("finished_reading_at")
    val finishedReadingAt: String? = null,

    @SerialName("created_at")
    val createdAt: String,
)