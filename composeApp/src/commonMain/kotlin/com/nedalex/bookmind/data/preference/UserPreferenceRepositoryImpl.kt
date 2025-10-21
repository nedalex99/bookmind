package com.nedalex.bookmind.data.preference

import com.nedalex.bookmind.domain.preference.AuthorEntity
import com.nedalex.bookmind.domain.preference.BookEntity
import com.nedalex.bookmind.domain.preference.PreferencesRepository
import com.nedalex.bookmind.domain.preference.UserPreferencesEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.benasher44.uuid.uuid4
import com.nedalex.bookmind.domain.preference.PaginatedRecommendations
import com.nedalex.bookmind.domain.preference.RecommendationTier
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class PreferencesRepositoryImpl(
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient,
    private val aiService: PreferencesAIService? = null
) : PreferencesRepository {

    // Cache to avoid re-fetching AI recommendations
    private var cachedAIAuthors: List<AuthorEntity>? = null
    private var cachedAIBooks: List<BookEntity>? = null

    // Clear cache when needed
    fun clearRecommendationCache() {
        cachedAIAuthors = null
        cachedAIBooks = null
    }

    // ============================================
    // Save/Get User Preferences
    // ============================================

    @OptIn(ExperimentalTime::class)
    override suspend fun saveUserPreferences(
        genres: List<String>,
        authorIds: List<String>,
        bookIds: List<String>,
        readingGoal: Int
    ) {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("User not authenticated")

        val preferences = UserPreferences(
            userId = userId,
            favoriteGenres = genres,
            favoriteAuthors = authorIds,
            booksRead = bookIds,
            completedAt = Clock.System.now().toString()
        )

        supabase.postgrest["user_preferences"].upsert(preferences)
    }

    override suspend fun getUserPreferences(): UserPreferencesEntity? {
        val userId = supabase.auth.currentUserOrNull()?.id ?: return null

        return supabase.postgrest["user_preferences"]
            .select(
                request = {
                    filter {
                        eq("user_id", userId)
                    }
                }
            )
            .decodeSingleOrNull<UserPreferencesDto>()
            ?.toDomain()
    }

    override suspend fun hasCompletedSetup(): Boolean {
        val prefs = getUserPreferences()
        return prefs?.completedAt != null
    }

    // ============================================
    // Smart Recommendations (Hybrid Approach)
    // ============================================

    override suspend fun getSmartAuthorRecommendations(
        genres: List<String>,
        offset: Int,
        limit: Int,
        useAI: Boolean
    ): PaginatedRecommendations<AuthorEntity> {

        // Phase 1: AI Recommendations (0-10)
        if (offset == 0 && useAI && aiService != null && genres.isNotEmpty()) {
            try {
                if (cachedAIAuthors == null) {
                    println("🤖 Fetching AI author recommendations")
                    cachedAIAuthors = getAIAuthorRecommendations(genres, limit = 10)
                    println("✅ Cached ${cachedAIAuthors?.size} AI recommendations")
                }

                val aiResults = cachedAIAuthors ?: emptyList()
                if (aiResults.isNotEmpty()) {
                    return PaginatedRecommendations(
                        items = aiResults.take(limit),
                        hasMore = true, // Always has more (filtered results next)
                        nextOffset = aiResults.size,
                        tier = RecommendationTier.AI_CURATED
                    )
                }
            } catch (e: Exception) {
                println("⚠️ AI recommendations failed: ${e.message}")
            }
        }

        // Phase 2: Genre-Filtered DB Results (10-30)
        val aiCount = cachedAIAuthors?.size ?: 0
        if (offset < aiCount + 20) {
            try {
                println("📚 Fetching genre-filtered authors (offset: $offset)")
                val dbAuthors = getAuthorsByGenres(genres, limit = 30)

                // Exclude AI-recommended authors
                val existingIds = cachedAIAuthors?.map { it.id }?.toSet() ?: emptySet()
                val filteredAuthors = dbAuthors.filter { it.id !in existingIds }

                // Calculate pagination
                val relativeOffset = maxOf(0, offset - aiCount)
                val paginatedResults = filteredAuthors
                    .drop(relativeOffset)
                    .take(limit)

                return PaginatedRecommendations(
                    items = paginatedResults,
                    hasMore = filteredAuthors.size > relativeOffset + limit || paginatedResults.size == limit,
                    nextOffset = offset + paginatedResults.size,
                    tier = RecommendationTier.GENRE_FILTERED
                )
            } catch (e: Exception) {
                println("⚠️ Filtered recommendations failed: ${e.message}")
            }
        }

        // Phase 3: Popular Fallback (30+)
        try {
            println("⭐ Fetching popular authors as fallback")
            val popularAuthors = getPopularAuthors(limit = 20)

            // Exclude already shown authors
            val existingIds = cachedAIAuthors?.map { it.id }?.toSet() ?: emptySet()
            val newPopular = popularAuthors.filter { it.id !in existingIds }

            val relativeOffset = maxOf(0, offset - aiCount - 20)
            val paginatedResults = newPopular
                .drop(relativeOffset)
                .take(limit)

            return PaginatedRecommendations(
                items = paginatedResults,
                hasMore = newPopular.size > relativeOffset + limit,
                nextOffset = offset + paginatedResults.size,
                tier = RecommendationTier.POPULAR
            )
        } catch (e: Exception) {
            println("❌ All recommendations failed: ${e.message}")
            return PaginatedRecommendations(
                items = emptyList(),
                hasMore = false,
                nextOffset = offset,
                tier = RecommendationTier.POPULAR
            )
        }
    }

    override suspend fun getSmartBookRecommendations(
        genres: List<String>,
        authorIds: List<String>,
        offset: Int,
        limit: Int,
        useAI: Boolean
    ): PaginatedRecommendations<BookEntity> {

        // Phase 1: AI Recommendations (0-10)
        if (offset == 0 && useAI && aiService != null && authorIds.isNotEmpty()) {
            try {
                if (cachedAIBooks == null) {
                    println("🤖 Fetching AI book recommendations")
                    cachedAIBooks = getAIBookRecommendations(genres, authorIds, limit = 10)
                    println("✅ Cached ${cachedAIBooks?.size} AI book recommendations")
                }

                val aiResults = cachedAIBooks ?: emptyList()
                if (aiResults.isNotEmpty()) {
                    return PaginatedRecommendations(
                        items = aiResults.take(limit),
                        hasMore = true,
                        nextOffset = aiResults.size,
                        tier = RecommendationTier.AI_CURATED
                    )
                }
            } catch (e: Exception) {
                println("⚠️ AI book recommendations failed: ${e.message}")
            }
        }

        // Phase 2: Filtered DB Results
        val aiCount = cachedAIBooks?.size ?: 0
        if (offset < aiCount + 30) {
            try {
                println("📚 Fetching filtered books (offset: $offset)")
                val dbBooks = getBooksByAuthorsAndGenres(authorIds, genres, limit = 40)

                val existingIds = cachedAIBooks?.map { it.id }?.toSet() ?: emptySet()
                val filteredBooks = dbBooks.filter { it.id !in existingIds }

                val relativeOffset = maxOf(0, offset - aiCount)
                val paginatedResults = filteredBooks
                    .drop(relativeOffset)
                    .take(limit)

                return PaginatedRecommendations(
                    items = paginatedResults,
                    hasMore = filteredBooks.size > relativeOffset + limit || paginatedResults.size == limit,
                    nextOffset = offset + paginatedResults.size,
                    tier = RecommendationTier.GENRE_FILTERED
                )
            } catch (e: Exception) {
                println("⚠️ Filtered books failed: ${e.message}")
            }
        }

        // Phase 3: Popular Fallback
        try {
            println("⭐ Fetching popular books as fallback")
            val popularBooks = getPopularBooks(limit = 20)

            val existingIds = cachedAIBooks?.map { it.id }?.toSet() ?: emptySet()
            val newPopular = popularBooks.filter { it.id !in existingIds }

            val relativeOffset = maxOf(0, offset - aiCount - 30)
            val paginatedResults = newPopular
                .drop(relativeOffset)
                .take(limit)

            return PaginatedRecommendations(
                items = paginatedResults,
                hasMore = newPopular.size > relativeOffset + limit,
                nextOffset = offset + paginatedResults.size,
                tier = RecommendationTier.POPULAR
            )
        } catch (e: Exception) {
            println("❌ All book recommendations failed: ${e.message}")
            return PaginatedRecommendations(
                items = emptyList(),
                hasMore = false,
                nextOffset = offset,
                tier = RecommendationTier.POPULAR
            )
        }
    }

    // ============================================
    // AI-Powered Recommendations
    // ============================================

    private suspend fun getAIAuthorRecommendations(
        genres: List<String>,
        limit: Int
    ): List<AuthorEntity> {
        val recommendedNames = aiService?.generateAuthorRecommendations(genres, limit * 2)
            ?: return emptyList()

        if (recommendedNames.isEmpty()) return emptyList()

        val authors = mutableListOf<AuthorEntity>()

        // Search for AI-recommended authors in DB and Google Books
        recommendedNames.forEach { name ->
            val found = searchAuthors(name, limit = 1)
            if (found.isNotEmpty()) {
                authors.add(found.first())
            }
            if (authors.size >= limit) return@forEach
        }

        return authors
    }

    private suspend fun getAIBookRecommendations(
        genres: List<String>,
        authorIds: List<String>,
        limit: Int
    ): List<BookEntity> {
        // Get author names from IDs
        val authorNames = authorIds.mapNotNull { authorId ->
            supabase.postgrest["authors"]
                .select {
                    filter { eq("id", authorId) }
                }
                .decodeSingleOrNull<AuthorDto>()?.name
        }

        val recommendedTitles = aiService?.generateBookRecommendations(genres, authorNames, limit * 2)
            ?: return emptyList()

        if (recommendedTitles.isEmpty()) return emptyList()

        val books = mutableListOf<BookEntity>()

        // Search for AI-recommended books
        recommendedTitles.forEach { title ->
            val found = searchBooks(title, limit = 1)
            if (found.isNotEmpty()) {
                books.add(found.first())
            }
            if (books.size >= limit) return@forEach
        }

        return books
    }

    // ============================================
    // Database Filtering (Fallback) (Using SQL Functions)
    // ============================================

    private suspend fun getAuthorsByGenres(genres: List<String>, limit: Int): List<AuthorEntity> {
        if (genres.isEmpty()) {
            return getPopularAuthors(limit)
        }

        return try {
            println("📞 Calling RPC: get_authors_by_genres")
            println("   Genres: ${genres.joinToString(", ")}")
            println("   Limit: $limit")

            val result = supabase.postgrest.rpc(
                function = "get_authors_by_genres",
                parameters = buildJsonObject {
                    put("genre_list", JsonArray(genres.map { JsonPrimitive(it) }))
                    put("result_limit", JsonPrimitive(limit))
                }
            )

            val authors = result.decodeList<AuthorRpcDto>()
                .map { it.toDomain() }

            println("✅ RPC returned ${authors.size} authors")

            if (authors.isEmpty()) {
                println("⚠️ No authors found for genres: ${genres.joinToString()}")
                println("   Falling back to popular authors")
                return getPopularAuthors(limit)
            }

            authors
        } catch (e: Exception) {
            println("❌ RPC error in get_authors_by_genres: ${e.message}")
            e.printStackTrace()
            // Fallback to popular authors
            println("   Falling back to popular authors")
            getPopularAuthors(limit)
        }
    }

    private suspend fun getBooksByAuthorsAndGenres(
        authorIds: List<String>,
        genres: List<String>,
        limit: Int
    ): List<BookEntity> {
        return try {
            println("📞 Calling RPC: get_books_by_authors_and_genres")
            println("   Author IDs: ${authorIds.size} authors")
            println("   Genres: ${genres.joinToString(", ")}")
            println("   Limit: $limit")

            val result = supabase.postgrest.rpc(
                function = "get_books_by_authors_and_genres",
                parameters = buildJsonObject {
                    put("author_id_list", JsonArray(authorIds.map { JsonPrimitive(it) }))
                    put("genre_list", JsonArray(genres.map { JsonPrimitive(it) }))
                    put("result_limit", JsonPrimitive(limit))
                }
            )

            val books = result.decodeList<BookRpcDto>()
                .map { it.toDomain() }

            println("✅ RPC returned ${books.size} books")

            if (books.isEmpty()) {
                println("⚠️ No books found")
                println("   Author IDs: ${authorIds.joinToString()}")
                println("   Genres: ${genres.joinToString()}")
                println("   Falling back to popular books")
                return getPopularBooks(limit)
            }

            books
        } catch (e: Exception) {
            println("❌ RPC error in get_books_by_authors_and_genres: ${e.message}")
            e.printStackTrace()
            println("   Falling back to popular books")
            getPopularBooks(limit)
        }
    }

    // ============================================
    // Backward Compatible Methods (Deprecated)
    // ============================================

    override suspend fun getPopularAuthors(limit: Int): List<AuthorEntity> {
        return supabase.postgrest["authors"]
            .select(
                request = {
                    order("created_at", order = Order.DESCENDING)
                    limit(limit.toLong())
                }
            )
            .decodeList<AuthorDto>()
            .map { it.toDomain(isFromDatabase = true) }
    }

    override suspend fun getPopularBooks(limit: Int): List<BookEntity> {
        return supabase.postgrest["books"]
            .select(
                request = {
                    order("rating", order = Order.DESCENDING)
                    limit(limit.toLong())
                }
            )
            .decodeList<BookDto>()
            .map { it.toDomain(isFromDatabase = true) }
    }

    // ============================================
    // Search Methods (Hybrid - DB + Google Books)
    // ============================================

    override suspend fun searchAuthors(query: String, limit: Int): List<AuthorEntity> {
        if (query.isBlank()) return emptyList()

        val localAuthors = supabase.postgrest["authors"]
            .select(
                request = {
                    filter {
                        ilike("name", "%$query%")
                    }
                    limit(limit.toLong())
                }
            )
            .decodeList<AuthorDto>()
            .map { it.toDomain(isFromDatabase = true) }

        if (localAuthors.size >= limit) {
            return localAuthors
        }

        val googleAuthors = searchAuthorsFromGoogleBooks(query, limit - localAuthors.size)
        return localAuthors + googleAuthors
    }

    override suspend fun searchBooks(query: String, limit: Int): List<BookEntity> {
        if (query.isBlank()) return emptyList()

        val localBooks = supabase.postgrest["books"]
            .select(
                request = {
                    filter {
                        or {
                            ilike("title", "%$query%")
                            ilike("author", "%$query%")
                        }
                    }
                    limit(limit.toLong())
                }
            )
            .decodeList<BookDto>()
            .map { it.toDomain(isFromDatabase = true) }

        if (localBooks.size >= limit) {
            return localBooks
        }

        val googleBooks = searchBooksFromGoogleBooks(query, limit - localBooks.size)
        return localBooks + googleBooks
    }

    // ============================================
    // Save When Selected
    // ============================================

    override suspend fun saveSelectedAuthor(author: AuthorEntity) {
        if (author.isFromDatabase) return

        try {
            val authorWithBooks = searchAuthorsWithBooksFromGoogle(author.name, 1)
            if (authorWithBooks.isNotEmpty()) {
                val (authorName, books) = authorWithBooks.first()
                addAuthorWithBooks(authorName, books)
            } else {
                addAuthorToDatabase(author.name)
            }
        } catch (e: Exception) {
            println("Error saving selected author: ${e.message}")
            e.printStackTrace()
        }
    }

    override suspend fun saveSelectedBook(book: BookEntity) {
        if (book.isFromDatabase) return

        try {
            addBookWithAuthor(book)
        } catch (e: Exception) {
            println("Error saving selected book: ${e.message}")
            e.printStackTrace()
        }
    }

    // ============================================
    // Google Books API Integration
    // ============================================

    private suspend fun searchAuthorsFromGoogleBooks(query: String, limit: Int): List<AuthorEntity> {
        return try {
            val response = httpClient.get("https://www.googleapis.com/books/v1/volumes") {
                parameter("q", "inauthor:$query")
                parameter("maxResults", minOf(limit * 2, 40))
            }

            val googleResponse = response.body<GoogleBooksResponse>()

            googleResponse.items
                ?.mapNotNull { it.volumeInfo.authors }
                ?.flatten()
                ?.distinct()
                ?.filter { it.contains(query, ignoreCase = true) }
                ?.take(limit)
                ?.map { authorName ->
                    AuthorEntity(
                        id = uuid4().toString(),
                        name = authorName,
                        notableBooks = "",
                        isFromDatabase = false
                    )
                } ?: emptyList()
        } catch (e: Exception) {
            println("Error searching Google Books for authors: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun searchAuthorsWithBooksFromGoogle(
        authorName: String,
        limit: Int = 1
    ): List<Pair<String, List<BookEntity>>> {
        return try {
            val response = httpClient.get("https://www.googleapis.com/books/v1/volumes") {
                parameter("q", "inauthor:$authorName")
                parameter("maxResults", 40)
            }

            val googleResponse = response.body<GoogleBooksResponse>()
            val authorBooksMap = mutableMapOf<String, MutableList<BookEntity>>()

            googleResponse.items?.forEach { item ->
                val author = item.volumeInfo.authors?.firstOrNull() ?: return@forEach
                if (!author.equals(authorName, ignoreCase = true)) return@forEach

                val book = BookEntity(
                    id = item.id,
                    title = item.volumeInfo.title,
                    author = author,
                    description = item.volumeInfo.description ?: "",
                    rating = item.volumeInfo.averageRating ?: 0.0,
                    pageCount = item.volumeInfo.pageCount ?: 0,
                    similarBooks = item.volumeInfo.categories ?: emptyList(),
                    coverUrl = item.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
                    isFromDatabase = false
                )

                authorBooksMap.getOrPut(author) { mutableListOf() }.add(book)
            }

            authorBooksMap.entries
                .take(limit)
                .map { (name, books) -> name to books }
        } catch (e: Exception) {
            println("Error searching authors with books from Google: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun searchBooksFromGoogleBooks(query: String, limit: Int): List<BookEntity> {
        return try {
            val response = httpClient.get("https://www.googleapis.com/books/v1/volumes") {
                parameter("q", query)
                parameter("maxResults", limit)
            }

            val googleResponse = response.body<GoogleBooksResponse>()

            googleResponse.items?.map { item ->
                BookEntity(
                    id = item.id,
                    title = item.volumeInfo.title,
                    author = item.volumeInfo.authors?.firstOrNull() ?: "Unknown",
                    description = item.volumeInfo.description ?: "",
                    rating = item.volumeInfo.averageRating ?: 0.0,
                    pageCount = item.volumeInfo.pageCount ?: 0,
                    similarBooks = item.volumeInfo.categories ?: emptyList(),
                    coverUrl = item.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
                    isFromDatabase = false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            println("Error searching Google Books: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // ============================================
    // Database Operations
    // ============================================

    private suspend fun addAuthorWithBooks(authorName: String, books: List<BookEntity>) {
        try {
            val existingAuthor = supabase.postgrest["authors"]
                .select {
                    filter {
                        eq("name", authorName)
                    }
                }
                .decodeSingleOrNull<AuthorDto>()

            val authorId = if (existingAuthor != null) {
                existingAuthor.id
            } else {
                val newAuthorId = uuid4().toString()
                val authorInsert = AuthorInsertDto(
                    id = newAuthorId,
                    name = authorName,
                    notableBooks = ""
                )
                supabase.postgrest["authors"].insert(authorInsert)
                newAuthorId
            }

            val bookTitles = mutableListOf<String>()

            books.forEach { book ->
                val bookExists = supabase.postgrest["books"]
                    .select {
                        filter {
                            eq("google_books_id", book.id)
                        }
                    }
                    .decodeSingleOrNull<BookDto>()

                if (bookExists == null) {
                    val bookId = uuid4().toString()

                    val bookInsert = BookInsertDto(
                        id = bookId,
                        googleBooksId = book.id,
                        title = book.title,
                        author = book.author,
                        authorId = authorId,
                        description = book.description,
                        rating = book.rating,
                        pageCount = book.pageCount,
                        coverUrl = book.coverUrl,
                        similarBooks = book.similarBooks
                    )

                    supabase.postgrest["books"].insert(bookInsert)
                    bookTitles.add(book.title)
                }
            }

            if (bookTitles.isNotEmpty()) {
                val existingNotableBooks = existingAuthor?.notable_books
                    ?.split(", ")
                    ?.filter { it.isNotBlank() }
                    ?: emptyList()

                val allBooks = (existingNotableBooks + bookTitles).distinct()
                val notableBooks = allBooks.take(5).joinToString(", ")

                val authorUpdate = AuthorUpdateDto(notableBooks = notableBooks)
                supabase.postgrest["authors"].update(authorUpdate) {
                    filter {
                        eq("id", authorId)
                    }
                }
            }
        } catch (e: Exception) {
            println("Error adding author with books: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun addAuthorToDatabase(authorName: String) {
        try {
            val existing = supabase.postgrest["authors"]
                .select {
                    filter {
                        eq("name", authorName)
                    }
                }
                .decodeSingleOrNull<AuthorDto>()

            if (existing != null) return

            val authorId = uuid4().toString()
            val authorInsert = AuthorInsertDto(
                id = authorId,
                name = authorName,
                notableBooks = ""
            )
            supabase.postgrest["authors"].insert(authorInsert)
        } catch (e: Exception) {
            println("Error adding author to database: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun addBookWithAuthor(book: BookEntity) {
        try {
            val existingBook = supabase.postgrest["books"]
                .select {
                    filter {
                        eq("google_books_id", book.id)
                    }
                }
                .decodeSingleOrNull<BookDto>()

            if (existingBook != null) return

            val authorId = getOrCreateAuthor(book.author)
            val bookId = uuid4().toString()

            val bookInsert = BookInsertDto(
                id = bookId,
                googleBooksId = book.id,
                title = book.title,
                author = book.author,
                authorId = authorId,
                description = book.description,
                rating = book.rating,
                pageCount = book.pageCount,
                coverUrl = book.coverUrl,
                similarBooks = book.similarBooks
            )

            supabase.postgrest["books"].insert(bookInsert)
            updateAuthorNotableBooks(authorId, book.title)
        } catch (e: Exception) {
            println("Error adding book with author: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun getOrCreateAuthor(authorName: String): String {
        val existing = supabase.postgrest["authors"]
            .select {
                filter {
                    eq("name", authorName)
                }
            }
            .decodeSingleOrNull<AuthorDto>()

        if (existing != null) {
            return existing.id
        }

        val authorId = uuid4().toString()
        val authorInsert = AuthorInsertDto(
            id = authorId,
            name = authorName,
            notableBooks = ""
        )
        supabase.postgrest["authors"].insert(authorInsert)

        return authorId
    }

    private suspend fun updateAuthorNotableBooks(authorId: String, bookTitle: String) {
        try {
            val author = supabase.postgrest["authors"]
                .select {
                    filter {
                        eq("id", authorId)
                    }
                }
                .decodeSingleOrNull<AuthorDto>() ?: return

            val currentBooks = author.notable_books?.split(", ")?.filter { it.isNotBlank() } ?: emptyList()
            val updatedBooks = (currentBooks + bookTitle).distinct().take(5).joinToString(", ")

            val authorUpdate = AuthorUpdateDto(notableBooks = updatedBooks)
            supabase.postgrest["authors"].update(authorUpdate) {
                filter {
                    eq("id", authorId)
                }
            }
        } catch (e: Exception) {
            println("Error updating author notable books: ${e.message}")
            e.printStackTrace()
        }
    }
}

// ============================================
// DTOs for Reading from Database
// ============================================

@Serializable
private data class UserPreferencesDto(
    val id: String,
    val user_id: String,
    val favorite_genres: List<String> = emptyList(),
    val favorite_authors: List<String> = emptyList(),
    val books_read: List<String> = emptyList(),
    val reading_goal: Int = 24,
    val completed_at: String? = null
) {
    fun toDomain() = UserPreferencesEntity(
        id = id,
        userId = user_id,
        favoriteGenres = favorite_genres,
        favoriteAuthors = favorite_authors,
        booksRead = books_read,
        readingGoal = reading_goal,
        completedAt = completed_at
    )
}

@Serializable
private data class AuthorDto(
    val id: String,
    val name: String,
    val notable_books: String? = null
) {
    fun toDomain(isFromDatabase: Boolean = true) = AuthorEntity(
        id = id,
        name = name,
        notableBooks = notable_books ?: "",
        isFromDatabase = isFromDatabase
    )
}

@Serializable
private data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    val rating: Double? = null,
    val page_count: Int? = null,
    val description: String? = null,
    val similar_books: List<String>? = null,
    val cover_url: String? = null,
    val google_books_id: String? = null
) {
    fun toDomain(isFromDatabase: Boolean = true) = BookEntity(
        id = id,
        title = title,
        author = author,
        rating = rating ?: 0.0,
        pageCount = page_count ?: 0,
        description = description ?: "",
        similarBooks = similar_books ?: emptyList(),
        coverUrl = cover_url,
        isFromDatabase = isFromDatabase
    )
}

// DTO for filtering books by author with nested query
@Serializable
private data class BookWithAuthorDto(
    @SerialName("author_id") val authorId: String? = null,
    val authors: NestedAuthorDto? = null
)

@Serializable
private data class NestedAuthorDto(
    val id: String,
    val name: String,
    @SerialName("notable_books") val notableBooks: String? = null
)

// ============================================
// DTOs for Writing to Database (Insert/Update)
// ============================================

@Serializable
private data class AuthorInsertDto(
    val id: String,
    val name: String,
    @SerialName("notable_books") val notableBooks: String
)

@Serializable
private data class AuthorUpdateDto(
    @SerialName("notable_books") val notableBooks: String
)

@Serializable
private data class BookInsertDto(
    val id: String,
    @SerialName("google_books_id") val googleBooksId: String,
    val title: String,
    val author: String,
    @SerialName("author_id") val authorId: String,
    val description: String,
    val rating: Double,
    @SerialName("page_count") val pageCount: Int,
    @SerialName("cover_url") val coverUrl: String?,
    @SerialName("similar_books") val similarBooks: List<String>
)

// ============================================
// User Preferences DTO
// ============================================

@Serializable
data class UserPreferences(
    @SerialName("user_id") val userId: String,
    @SerialName("favorite_genres") val favoriteGenres: List<String>,
    @SerialName("favorite_authors") val favoriteAuthors: List<String>,
    @SerialName("books_read") val booksRead: List<String>,
    @SerialName("completed_at") val completedAt: String
)

// ============================================
// Google Books API DTOs
// ============================================

@Serializable
data class GoogleBooksResponse(
    val items: List<BookItem>?
)

@Serializable
data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

@Serializable
data class VolumeInfo(
    val title: String,
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null,
    val categories: List<String>? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val averageRating: Double? = null
)

@Serializable
data class ImageLinks(
    val thumbnail: String? = null,
    val smallThumbnail: String? = null
)

// RPC Response DTOs
@Serializable
private data class AuthorRpcDto(
    val id: String,
    val name: String,
    val notable_books: String? = null,
    val created_at: String? = null
) {
    fun toDomain() = AuthorEntity(
        id = id,
        name = name,
        notableBooks = notable_books ?: "",
        isFromDatabase = true
    )
}

@Serializable
private data class BookRpcDto(
    val id: String,
    val google_books_id: String? = null,
    val title: String,
    val author: String,
    val author_id: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val page_count: Int? = null,
    val cover_url: String? = null,
    val similar_books: List<String>? = null,
    val genre: String? = null
) {
    fun toDomain() = BookEntity(
        id = id,
        title = title,
        author = author,
        rating = rating ?: 0.0,
        pageCount = page_count ?: 0,
        description = description ?: "",
        similarBooks = similar_books ?: emptyList(),
        coverUrl = cover_url,
        isFromDatabase = true
    )
}