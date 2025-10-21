package com.nedalex.bookmind.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("author")
    val author: String,

    @SerialName("isbn")
    val isbn: String? = null,

    @SerialName("cover_image_url")
    val coverImageUrl: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("genres")
    val genres: List<String> = emptyList(),

    @SerialName("published_year")
    val publishedYear: Int? = null,

    @SerialName("page_count")
    val pageCount: Int? = null,

    @SerialName("average_rating")
    val averageRating: Double = 0.0,

    @SerialName("ratings_count")
    val ratingsCount: Int = 0,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("added_by_user_id")
    val addedByUserId: String? = null,
)

object PreviewBooks {
    val mockBooks = listOf(
        Book(
            id = "1",
            title = "The Midnight Library",
            author = "Matt Haig",
            isbn = "9780525559474",
            coverImageUrl = "https://images.example.com/midnight-library.jpg",
            description = "Between life and death there is a library, and within that library, the shelves go on forever. Every book provides a chance to try another life you could have lived.",
            genres = listOf("Fiction", "Fantasy", "Philosophy"),
            publishedYear = 2020,
            pageCount = 304,
            averageRating = 4.2,
            ratingsCount = 125430,
            createdAt = "2024-01-15T10:30:00Z",
            addedByUserId = "user123"
        ),
        Book(
            id = "2",
            title = "Project Hail Mary",
            author = "Andy Weir",
            isbn = "9780593135204",
            coverImageUrl = "https://images.example.com/project-hail-mary.jpg",
            description = "A lone astronaut must save the earth from disaster in this incredible new science-based thriller from the author of The Martian.",
            genres = listOf("Science Fiction", "Thriller", "Adventure"),
            publishedYear = 2021,
            pageCount = 496,
            averageRating = 4.6,
            ratingsCount = 89250,
            createdAt = "2024-02-20T14:15:00Z",
            addedByUserId = "user456"
        ),
        Book(
            id = "3",
            title = "Atomic Habits",
            author = "James Clear",
            isbn = "9780735211292",
            coverImageUrl = "https://images.example.com/atomic-habits.jpg",
            description = "An easy and proven way to build good habits and break bad ones.",
            genres = listOf("Self-Help", "Psychology", "Productivity"),
            publishedYear = 2018,
            pageCount = 320,
            averageRating = 4.4,
            ratingsCount = 203500,
            createdAt = "2024-01-05T09:00:00Z",
            addedByUserId = "user789"
        ),
        Book(
            id = "4",
            title = "The Song of Achilles",
            author = "Madeline Miller",
            isbn = "9780062060624",
            coverImageUrl = "https://images.example.com/song-of-achilles.jpg",
            description = "A tale of gods, kings, immortal fame and the human heart, The Song of Achilles is a dazzling literary feat.",
            genres = listOf("Historical Fiction", "Romance", "Mythology"),
            publishedYear = 2011,
            pageCount = 352,
            averageRating = 4.5,
            ratingsCount = 156890,
            createdAt = "2024-03-10T16:45:00Z",
            addedByUserId = "user123"
        ),
        Book(
            id = "5",
            title = "1984",
            author = "George Orwell",
            isbn = "9780451524935",
            coverImageUrl = "https://images.example.com/1984.jpg",
            description = "A dystopian social science fiction novel and cautionary tale about the dangers of totalitarianism.",
            genres = listOf("Dystopian", "Science Fiction", "Classic"),
            publishedYear = 1949,
            pageCount = 328,
            averageRating = 4.3,
            ratingsCount = 450200,
            createdAt = "2024-01-01T08:00:00Z",
            addedByUserId = "user456"
        ),
        Book(
            id = "6",
            title = "The Silent Patient",
            author = "Alex Michaelides",
            isbn = "9781250301697",
            coverImageUrl = "https://images.example.com/silent-patient.jpg",
            description = "A woman's act of violence against her husband—and of the therapist obsessed with uncovering her motive.",
            genres = listOf("Thriller", "Mystery", "Psychological"),
            publishedYear = 2019,
            pageCount = 336,
            averageRating = 4.1,
            ratingsCount = 98760,
            createdAt = "2024-02-28T11:20:00Z",
            addedByUserId = "user789"
        ),
        Book(
            id = "7",
            title = "Educated",
            author = "Tara Westover",
            isbn = "9780399590504",
            coverImageUrl = "https://images.example.com/educated.jpg",
            description = "A memoir about a young woman who, kept out of school, leaves her survivalist family and goes on to earn a PhD from Cambridge University.",
            genres = listOf("Memoir", "Biography", "Non-Fiction"),
            publishedYear = 2018,
            pageCount = 334,
            averageRating = 4.5,
            ratingsCount = 178920,
            createdAt = "2024-01-18T13:30:00Z",
            addedByUserId = "user123"
        ),
        Book(
            id = "8",
            title = "Dune",
            author = "Frank Herbert",
            isbn = "9780441172719",
            coverImageUrl = "https://images.example.com/dune.jpg",
            description = "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling an inhospitable world.",
            genres = listOf("Science Fiction", "Fantasy", "Adventure"),
            publishedYear = 1965,
            pageCount = 688,
            averageRating = 4.3,
            ratingsCount = 321450,
            createdAt = "2024-03-05T15:10:00Z",
            addedByUserId = "user456"
        )
    )
}