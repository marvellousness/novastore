package nova.android.novastore.feature.booklist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import nova.android.novastore.ui.theme.NovaStoreTheme
import nova.android.novastore.domain.model.Book
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import nova.android.novastore.feature.bookdetail.BookDetailActivity

@AndroidEntryPoint
class BookListActivity : ComponentActivity() {
	
	private val viewModel: BookListViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			NovaStoreTheme {
				BookListScreen(
					viewModel = viewModel,
					modifier = Modifier.fillMaxSize()
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
	viewModel: BookListViewModel,
	modifier: Modifier = Modifier
) {
	val uiState by viewModel.uiState.collectAsState()
	val books by viewModel.booksFlow.collectAsState(initial = emptyList())
	var searchQuery by remember { mutableStateOf("") }
	var isSearchVisible by remember { mutableStateOf(false) }
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		viewModel.effect.collect { effect ->
			when (effect) {
				is BookListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
			}
		}
	}
	
	Scaffold(
		modifier = modifier,
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { 
					if (isSearchVisible) {
						TextField(
							value = searchQuery,
							onValueChange = { 
								searchQuery = it
								if (it.isNotEmpty()) {
									viewModel.dispatch(BookListIntent.Search(it))
								} else {
									viewModel.dispatch(BookListIntent.Load())
								}
							},
							placeholder = { Text("Search books...") },
							modifier = Modifier.fillMaxWidth(),
							singleLine = true
						)
					} else {
						Text("NovaStore - Books")
					}
				},
				actions = {
					if (!isSearchVisible) {
						IconButton(onClick = { isSearchVisible = true }) {
							Icon(
								imageVector = Icons.Default.Search,
								contentDescription = "Search"
							)
						}
					}
					IconButton(onClick = { viewModel.dispatch(BookListIntent.Load(forceRefresh = true)) }) {
						Icon(
							imageVector = Icons.Default.Refresh,
							contentDescription = "Refresh"
						)
					}
				}
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = { /* TODO: Add new book */ }) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Add Book"
				)
			}
		}
	) { innerPadding ->
		BookListContent(
			books = books,
			uiState = uiState,
			onRetry = { viewModel.dispatch(BookListIntent.Retry) },
			onRefresh = { viewModel.dispatch(BookListIntent.Load(forceRefresh = true)) },
			modifier = Modifier.padding(innerPadding)
		)
	}
}

@Composable

fun BookListContent(
	books: List<Book>,
	uiState: BookListState,
	onRetry: () -> Unit,
	onRefresh: () -> Unit,
	modifier: Modifier = Modifier
) {
	when {
		uiState.isLoading && books.isEmpty() -> {
			LoadingState(modifier = modifier)
		}
		uiState.hasError -> {
			ErrorState(
				error = uiState.error ?: "Unknown error occurred",
				onRetry = onRetry,
				modifier = modifier
			)
		}
		books.isEmpty() -> {
			EmptyState(
				onRefresh = onRefresh,
				modifier = modifier
			)
		}
		else -> {
			BookList(
				books = books,
				onRefresh = onRefresh,
				modifier = modifier,
				isRefreshing = uiState.isRefreshing
			)
		}
	}
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			CircularProgressIndicator()
			Text("Loading books...")
		}
	}
}

@Composable
fun ErrorState(
	error: String,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = "Error",
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.error
			)
			Text(
				text = error,
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Center,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Button(onClick = onRetry) {
				Text("Retry")
			}
		}
	}
}

@Composable
fun EmptyState(
	onRefresh: () -> Unit,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = "No Books Found",
				style = MaterialTheme.typography.headlineSmall
			)
			Text(
				text = "Start by adding some books or refreshing to load from server",
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Center,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Button(onClick = onRefresh) {
				Text("Refresh")
			}
		}
	}
}

@Composable
fun BookList(
	books: List<Book>,
	onRefresh: () -> Unit,
	modifier: Modifier = Modifier,
	isRefreshing: Boolean = false
) {
	val context = LocalContext.current
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(books) { book ->
			BookItem(book = book, onClick = { selected ->
				val intent = Intent(context, BookDetailActivity::class.java).apply {
					putExtra(BookDetailActivity.EXTRA_BOOK_ID, selected.id.toString())
				}
				context.startActivity(intent)
			})
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookItem(book: Book, onClick: (Book) -> Unit = {}) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick(book) },
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = book.title?: "Unknown Title",
				style = MaterialTheme.typography.titleMedium,
				color = MaterialTheme.colorScheme.onSurface
			)
			Text(
				text = "by ${book.author}",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			if (book.isRecentlyUpdated()) {
				Surface(
					color = MaterialTheme.colorScheme.primaryContainer,
					shape = MaterialTheme.shapes.small
				) {
					Text(
						text = "Recently Updated",
						modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}
	}
}


