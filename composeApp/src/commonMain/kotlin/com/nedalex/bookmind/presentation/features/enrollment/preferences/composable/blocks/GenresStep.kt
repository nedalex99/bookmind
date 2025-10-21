package com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nedalex.bookmind.domain.preference.AuthorEntity
import com.nedalex.bookmind.domain.preference.BookEntity
import com.nedalex.bookmind.domain.preference.RecommendationTier
import com.nedalex.bookmind.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

// ============================================
// GENRES STEP
// ============================================

@Composable
fun GenresStep(
    vs: PreferencesVS,
    act: (PreferencesAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📚 Genres",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "(${vs.selectedGenres.size} selected)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(600.dp), // Fixed height for nested scrolling
                userScrollEnabled = false
            ) {
                items(AvailableGenres.all) { genre ->
                    GenreCard(
                        genre = genre,
                        isSelected = vs.selectedGenres.contains(genre.id),
                        onClick = { act(PreferencesAction.GenreToggled(genre.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreCard(
    genre: Genre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = genre.emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = genre.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ============================================
// INFINITE SCROLL HANDLER
// ============================================

@Composable
fun InfiniteScrollHandler(
    listState: LazyListState,
    buffer: Int = 3,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItem >= totalItems - buffer
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================
// AUTHORS STEP
// ============================================

@Composable
fun AuthorsStep(
    vs: PreferencesVS,
    act: (PreferencesAction) -> Unit
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header and Search at the top
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✍️ Favorite Authors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "(${vs.selectedAuthors.size} added)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Field
            OutlinedTextField(
                value = vs.authorSearchQuery,
                onValueChange = { act(PreferencesAction.AuthorSearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search for authors...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = MaterialTheme.shapes.medium
            )
        }

        // Scrollable content
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selected Authors
            if (vs.selectedAuthors.isNotEmpty()) {
                items(vs.selectedAuthors.toList()) { author ->
                    SelectedAuthorCard(
                        author = author,
                        onRemove = { act(PreferencesAction.AuthorRemoved(author.id)) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Section header based on tier
            item {
                when (vs.currentAuthorTier) {
                    RecommendationTier.AI_CURATED -> {
                        SectionHeader(
                            title = "🌟 Recommended For You",
                            subtitle = "AI-curated picks based on your genres"
                        )
                    }
                    RecommendationTier.GENRE_FILTERED -> {
                        if (vs.authorOffset > 10) {
                            SectionHeader(
                                title = "📚 More Authors in Your Genres",
                                subtitle = null
                            )
                        } else {
                            Text(
                                text = "Suggestions",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    RecommendationTier.POPULAR -> {
                        if (vs.authorOffset > 30) {
                            SectionHeader(
                                title = "⭐ Popular Authors",
                                subtitle = null
                            )
                        }
                    }
                    null -> {
                        Text(
                            text = "Suggestions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Author suggestions
            items(
                items = vs.authorSuggestions,
                key = { it.id }
            ) { author ->
                AuthorSuggestionCard(
                    author = author,
                    onClick = { act(PreferencesAction.AuthorSelected(author)) }
                )
            }

            // Loading indicator or end message
            item {
                when {
                    vs.isLoadingMoreAuthors -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    !vs.hasMoreAuthors && vs.authorSuggestions.isNotEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "That's all the authors we have! 📚",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Infinite scroll trigger
        InfiniteScrollHandler(
            listState = listState,
            buffer = 3,
            onLoadMore = {
                if (vs.hasMoreAuthors && !vs.isLoadingMoreAuthors) {
                    act(PreferencesAction.LoadMoreAuthors)
                }
            }
        )
    }
}

@Composable
private fun SelectedAuthorCard(
    author: AuthorEntity,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = author.notableBooks,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AuthorSuggestionCard(
    author: AuthorEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = author.notableBooks,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = "+",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ============================================
// BOOKS STEP WITH INFINITE SCROLL
// ============================================

@Composable
fun BooksStep(
    vs: PreferencesVS,
    act: (PreferencesAction) -> Unit
) {
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header and Search
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 Books You've Read",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "(${vs.selectedBooks.size} selected)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Search Field
            OutlinedTextField(
                value = vs.bookSearchQuery,
                onValueChange = { act(PreferencesAction.BookSearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search for books...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = MaterialTheme.shapes.medium
            )
        }

        // Scrollable content
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section header
            item {
                when (vs.currentBookTier) {
                    RecommendationTier.AI_CURATED -> {
                        SectionHeader(
                            title = "🌟 Perfect Picks For You",
                            subtitle = "Based on your favorite authors and genres"
                        )
                    }
                    RecommendationTier.GENRE_FILTERED -> {
                        if (vs.bookOffset > 10) {
                            SectionHeader(
                                title = "📚 More Books You Might Like",
                                subtitle = null
                            )
                        }
                    }
                    RecommendationTier.POPULAR -> {
                        if (vs.bookOffset > 40) {
                            SectionHeader(
                                title = "⭐ Popular Books",
                                subtitle = null
                            )
                        }
                    }
                    null -> {}
                }
            }

            // Book suggestions
            items(
                items = vs.bookSuggestions,
                key = { it.id }
            ) { book ->
                BookCard(
                    book = book,
                    isSelected = vs.selectedBooks.any { it.id == book.id },
                    onClick = { act(PreferencesAction.BookToggled(book)) }
                )
            }

            // Loading/end
            item {
                when {
                    vs.isLoadingMoreBooks -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    !vs.hasMoreBooks && vs.bookSuggestions.isNotEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You've reached the end! 📖",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Infinite scroll trigger
        InfiniteScrollHandler(
            listState = listState,
            buffer = 3,
            onLoadMore = {
                if (vs.hasMoreBooks && !vs.isLoadingMoreBooks) {
                    act(PreferencesAction.LoadMoreBooks)
                }
            }
        )
    }
}

@Composable
private fun BookCard(
    book: BookEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Book Cover
            BookCover(
                coverUrl = book.coverUrl,
                bookTitle = book.title,
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Book Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "★",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = book.rating.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = "${book.pageCount} pages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = book.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontStyle = FontStyle.Italic,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (book.similarBooks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Similar books tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            book.similarBooks.take(3).forEach { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// GOAL STEP
// ============================================

@Composable
fun GoalStep(
    vs: PreferencesVS,
    act: (PreferencesAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "🎯",
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Reading Goal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Goal Number Display
        Text(
            text = vs.readingGoal.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "books per year",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Goal Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(12, 24).forEach { goal ->
                GoalOption(
                    goal = goal,
                    isSelected = vs.readingGoal == goal,
                    onClick = { act(PreferencesAction.GoalChanged(goal)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(36, 52).forEach { goal ->
                GoalOption(
                    goal = goal,
                    isSelected = vs.readingGoal == goal,
                    onClick = { act(PreferencesAction.GoalChanged(goal)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "You can always change this later in settings",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GoalOption(
    goal: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = goal.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "books/year",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// ============================================
// PREVIEWS
// ============================================

@Preview(showBackground = true)
@Composable
private fun GenresStepPreview() {
    ReadingAppTheme {
        GenresStep(
            vs = PreferencesVS(
                currentStep = PreferencesStep.GENRES,
                selectedGenres = setOf("fiction", "mystery")
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthorsStepPreview() {
    ReadingAppTheme {
        AuthorsStep(
            vs = PreferencesVS(
                currentStep = PreferencesStep.AUTHORS,
                selectedAuthors = setOf(
                    AuthorEntity("1", "Jane Austen", "Pride and Prejudice, Emma")
                ),
                authorSuggestions = listOf(
                    AuthorEntity("2", "F. Scott Fitzgerald", "The Great Gatsby")
                )
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BooksStepPreview() {
    ReadingAppTheme {
        BooksStep(
            vs = PreferencesVS(
                currentStep = PreferencesStep.BOOKS,
                bookSuggestions = listOf(
                    BookEntity(
                        id = "1",
                        title = "The Great Gatsby",
                        author = "F. Scott Fitzgerald",
                        rating = 4.5,
                        pageCount = 180,
                        description = "Classic American literature",
                        similarBooks = listOf("Classic", "Fiction"),
                        coverUrl = ""
                    )
                )
            ),
            act = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GoalStepPreview() {
    ReadingAppTheme {
        GoalStep(
            vs = PreferencesVS(
                currentStep = PreferencesStep.GOAL,
                readingGoal = 24
            ),
            act = {}
        )
    }
}