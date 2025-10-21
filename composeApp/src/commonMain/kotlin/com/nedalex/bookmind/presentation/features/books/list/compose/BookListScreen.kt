package com.nedalex.bookmind.presentation.features.books.list.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nedalex.bookmind.app.Route
import com.nedalex.bookmind.architecture.blocks.navigation.NavigationHandler
import com.nedalex.bookmind.data.models.Book
import com.nedalex.bookmind.data.models.PreviewBooks
import com.nedalex.bookmind.presentation.features.books.list.blocks.BookListVM
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListAction
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListNavigation
import com.nedalex.bookmind.presentation.features.books.list.blocks.model.BookListVS
import com.nedalex.bookmind.presentation.theme.ReadingAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BookListScreen(
    vm: BookListVM,
    navController: NavController
) {
    fun act(action: BookListAction) = vm.onAction(action)

    NavigationHandler(
        navController = navController,
        navigationFlow = vm.navigation,
        onNavigate = { event ->
            when (event) {
                is BookListNavigation.ToBookDetail ->
                    navController.navigate(Route.BookDetail(event.bookId))

                BookListNavigation.ToAddBook ->
                    navController.navigate(Route.AddBook)

                BookListNavigation.ToRecommendations ->
                    navController.navigate(Route.Recommendations)

                BookListNavigation.ToMyLibrary ->
                    navController.navigate(Route.MyLibrary)
            }
        }
    )

    Screen(vm.viewState, ::act)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    vs: BookListVS,
    act: (BookListAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BookMind") },
                actions = {
                    IconButton(onClick = { act(BookListAction.AddBookClicked) }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Add,
                            contentDescription = "Add Book"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { act(BookListAction.RecommendationsClicked) }
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Star,
                    contentDescription = "Recommendations"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = vs.searchQuery,
                onValueChange = { act(BookListAction.SearchBooks(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search books, authors...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            )

            // Error Message
            if (vs.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = vs.error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Loading or Content
            when {
                vs.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                vs.books.isEmpty() -> {
                    EmptyState(act)
                }

                else -> {
                    BooksList(books = vs.books, act = act)
                }
            }
        }
    }
}

@Composable
private fun BooksList(
    books: List<Book>,
    act: (BookListAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books) { book ->
            BookItem(book = book, onClick = { act(BookListAction.BookClicked(book.id)) })
        }
    }
}

@Composable
private fun BookItem(
    book: Book,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Book Cover
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Book Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (book.genres.isNotEmpty()) {
                    Text(
                        text = book.genres.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Rating
                if (book.averageRating > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = book.averageRating.let { (it * 10).toInt() / 10.0 }.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${book.ratingsCount})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(act: (BookListAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.MenuBook,
            contentDescription = "No books",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No books found",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start by adding your first book!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { act(BookListAction.AddBookClicked) }) {
            Text("Add Book")
        }
    }
}

@Composable
@Preview
private fun BookListScreenPreview() = ReadingAppTheme {
    Screen(vs = BookListVS(books = PreviewBooks.mockBooks), act = {})
}