package com.nedalex.bookmind.data.preference

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PreferencesAIService(
    private val httpClient: HttpClient,
    private val apiKey: String? = null // Optional - can be null for non-AI mode
) {

    suspend fun generateAuthorRecommendations(
        selectedGenres: List<String>,
        limit: Int = 20
    ): List<String> {
        if (apiKey.isNullOrBlank()) return emptyList()

        val prompt = """
            Based on these book genres: ${selectedGenres.joinToString(", ")}, 
            recommend $limit popular and diverse authors that readers of these genres would enjoy.
            Include both classic and contemporary authors.
            
            IMPORTANT: Return ONLY a valid JSON array of author names, with no additional text, markdown, or explanation.
            Format: ["Author Name 1", "Author Name 2", ...]
        """.trimIndent()

        return try {
            callClaudeAPI(prompt)
        } catch (e: Exception) {
            println("AI author recommendations failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun generateBookRecommendations(
        selectedGenres: List<String>,
        selectedAuthors: List<String>,
        limit: Int = 20
    ): List<String> {
        if (apiKey.isNullOrBlank()) return emptyList()

        val prompt = """
            Based on these reading preferences:
            - Genres: ${selectedGenres.joinToString(", ")}
            - Favorite Authors: ${selectedAuthors.joinToString(", ")}
            
            Recommend $limit must-read book titles that this person would love.
            Include a mix of classic and contemporary works.
            
            IMPORTANT: Return ONLY a valid JSON array of book titles, with no additional text, markdown, or explanation.
            Format: ["Book Title 1", "Book Title 2", ...]
        """.trimIndent()

        return try {
            callClaudeAPI(prompt)
        } catch (e: Exception) {
            println("AI book recommendations failed: ${e.message}")
            emptyList()
        }
    }

    private suspend fun callClaudeAPI(prompt: String): List<String> {
        val response = httpClient.post("https://api.anthropic.com/v1/messages") {
            headers {
                append("x-api-key", apiKey ?: "")
                append("anthropic-version", "2023-06-01")
            }
            contentType(ContentType.Application.Json)
            setBody(ClaudeRequest(
                model = "claude-sonnet-4-20250514",
                maxTokens = 1500,
                messages = listOf(
                    ClaudeMessage(
                        role = "user",
                        content = prompt
                    )
                )
            ))
        }

        val data = response.body<ClaudeResponse>()
        val text = data.content.firstOrNull()?.text ?: return emptyList()

        // Clean up response - remove markdown code blocks if present
        val cleanedText = text
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            Json.decodeFromString<List<String>>(cleanedText)
        } catch (e: Exception) {
            println("Failed to parse AI response: ${e.message}")
            println("Response was: $cleanedText")
            emptyList()
        }
    }
}

// DTOs for Claude API
@Serializable
private data class ClaudeRequest(
    val model: String,
    @kotlinx.serialization.SerialName("max_tokens")
    val maxTokens: Int,
    val messages: List<ClaudeMessage>
)

@Serializable
private data class ClaudeMessage(
    val role: String,
    val content: String
)

@Serializable
private data class ClaudeResponse(
    val content: List<ClaudeContent>
)

@Serializable
private data class ClaudeContent(
    val text: String
)