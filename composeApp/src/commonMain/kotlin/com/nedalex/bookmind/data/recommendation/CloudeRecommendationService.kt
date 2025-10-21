//package com.nedalex.bookmind.data.recommendation
//
//import com.nedalex.bookmind.data.models.BookRecommendation
//import com.nedalex.bookmind.domain.book.BookRepository
//import com.nedalex.bookmind.domain.preference.PreferencesRepository
//import com.nedalex.bookmind.domain.recommendation.AIRecommendationService
//import io.ktor.client.HttpClient
//import io.ktor.client.request.header
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.client.statement.bodyAsText
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import kotlinx.serialization.json.Json
//
///**
// * Implementation using Claude API
// */
//class ClaudeRecommendationService(
//    private val httpClient: HttpClient,
//    private val apiKey: String,
//    private val json: Json,
//    private val bookRepository: PreferencesRepository
//) : AIRecommendationService {
//
//    private val apiUrl = "https://api.anthropic.com/v1/messages"
//
//    override suspend fun getRecommendations(
//        genres: List<String>,
//        authorIds: List<String>,
//        booksRead: List<String>,
//        limit: Int
//    ): List<BookRecommendation> {
//
//        // Get author names from IDs
//        val authorNames = bookRepository.getAuthorsByIds(authorIds).map { it.name }
//
//        // Get book titles from IDs
//        val bookTitles = bookRepository.getBooksByIds(booksRead).map { "${it.title} by ${it.author}" }
//
//        val prompt = buildRecommendationPrompt(
//            genres = genres,
//            authors = authorNames,
//            booksRead = bookTitles,
//            limit = limit
//        )
//
//        val response = httpClient.post(apiUrl) {
//            header("x-api-key", apiKey)
//            header("anthropic-version", "2023-06-01")
//            contentType(ContentType.Application.Json)
//
//            setBody(json.encodeToString(
//                ClaudeRequest.serializer(),
//                ClaudeRequest(
//                    model = "claude-sonnet-4-20250514",
//                    maxTokens = 4000,
//                    messages = listOf(
//                        Message(
//                            role = "user",
//                            content = prompt
//                        )
//                    )
//                )
//            ))
//        }
//
//        val responseBody = response.bodyAsText()
//        val claudeResponse = json.decodeFromString<ClaudeResponse>(responseBody)
//
//        return parseRecommendations(claudeResponse.content.firstOrNull()?.text ?: "")
//    }
//
//    private fun buildRecommendationPrompt(
//        genres: List<String>,
//        authors: List<String>,
//        booksRead: List<String>,
//        limit: Int
//    ): String {
//        return """
//You are a book recommendation expert. Based on the user's reading preferences, recommend $limit books they would enjoy.
//
//USER PREFERENCES:
//- Favorite Genres: ${genres.joinToString(", ")}
//- Favorite Authors: ${authors.joinToString(", ")}
//- Books They've Read and Enjoyed: ${booksRead.joinToString(", ")}
//
//Please provide $limit book recommendations in the following JSON format. DO NOT include any text outside the JSON structure:
//
//[
//  {
//    "title": "Book Title",
//    "author": "Author Name",
//    "genre": "Primary Genre",
//    "rating": 4.5,
//    "matchPercentage": 95,
//    "reason": "A brief explanation of why this book matches their preferences",
//    "pageCount": 320,
//    "publicationYear": 2020,
//    "description": "A compelling description of the book",
//    "similarTo": ["Book 1", "Book 2"]
//  }
//]
//
//IMPORTANT:
//1. Only recommend real, published books
//2. Match percentage should reflect how well it fits their preferences (70-98%)
//3. Reasons should be specific and reference their stated preferences
//4. Include books similar to what they've read but not the exact same books
//5. Vary the recommendations across their favorite genres
//6. Your entire response must be valid JSON only - no markdown, no explanations, just the JSON array
//        """.trimIndent()
//    }
//
//    private fun parseRecommendations(response: String): List<BookRecommendation> {
//        return try {
//            // Remove markdown code blocks if present
//            val cleanJson = response
//                .replace("```json", "")
//                .replace("```", "")
//                .trim()
//
//            json.decodeFromString<List<BookRecommendation>>(cleanJson)
//        } catch (e: Exception) {
//            println("Error parsing recommendations: ${e.message}")
//            println("Response was: $response")
//            emptyList()
//        }
//    }
//}