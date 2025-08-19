package nova.android.novastore.feature.bookdetail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import nova.android.novastore.ui.theme.NovaStoreTheme

@AndroidEntryPoint
class BookDetailActivity : ComponentActivity() {

	private val viewModel: BookDetailViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val bookId = intent.getStringExtra(EXTRA_BOOK_ID) ?: ""
		setContent {
			NovaStoreTheme {
				BookDetailScreen(viewModel, bookId)
			}
		}
		viewModel.dispatch(BookDetailIntent.Load(bookId))
	}

	companion object {
		const val EXTRA_BOOK_ID = "book_id"
	}
}

@Composable
fun BookDetailScreen(viewModel: BookDetailViewModel, bookId: String) {
	val uiState by viewModel.uiState.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		viewModel.effect.collect { effect ->
			when (effect) {
				is BookDetailEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
			}
		}
	}

	Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
		when {
			uiState.isLoading -> LoadingView(modifier = Modifier.padding(paddingValues))
			uiState.book == null -> EmptyView(modifier = Modifier.padding(paddingValues))
			else -> DetailView(uiState, modifier = Modifier.padding(paddingValues))
		}
	}
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
	Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
		CircularProgressIndicator()
	}
}

@Composable
private fun EmptyView(modifier: Modifier = Modifier) {
	Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
		Text(text = "No details available", style = MaterialTheme.typography.bodyLarge)
	}
}

@Composable
private fun DetailView(state: BookDetailState, modifier: Modifier = Modifier) {
	Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(text = state.book?.title ?: "Unknown Title", style = MaterialTheme.typography.headlineSmall)
		Text(text = "by ${state.book?.author}", style = MaterialTheme.typography.bodyMedium)
	}
}


