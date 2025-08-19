package nova.android.novastore.feature.booklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nova.android.novastore.domain.usecase.ClearLocalDataUseCase
import nova.android.novastore.domain.usecase.GetBooksFlowUseCase
import nova.android.novastore.domain.usecase.GetBooksUseCase
import nova.android.novastore.domain.usecase.SearchBooksUseCase

@HiltViewModel
class BookViewModel @Inject constructor(
	private val getBooksUseCase: GetBooksUseCase,
	private val searchBooksUseCase: SearchBooksUseCase,
	private val clearLocalDataUseCase: ClearLocalDataUseCase,
	getBooksFlowUseCase: GetBooksFlowUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookListState())
	val uiState: StateFlow<BookListState> = _uiState.asStateFlow()

	// Reactive books stream
	val booksFlow: Flow<List<nova.android.novastore.domain.model.Book>> = getBooksFlowUseCase()

	// One-off effects
	private val _effect = Channel<BookListEffect>(Channel.BUFFERED)
	val effect: Flow<BookListEffect> = _effect.receiveAsFlow()

	init {
		dispatch(BookListIntent.Load())
	}

	fun dispatch(intent: BookListIntent) {
		when (intent) {
			is BookListIntent.Load -> load(forceRefresh = intent.forceRefresh)
			is BookListIntent.Search -> search(intent.query)
			BookListIntent.Retry -> load(forceRefresh = false)
			BookListIntent.ClearLocal -> clearLocal()
		}
	}

	private fun load(forceRefresh: Boolean) {
		viewModelScope.launch {
			if (forceRefresh) {
				_uiState.update { it.copy(isRefreshing = true) }
			} else {
				_uiState.update { it.copy(isLoading = true, error = null) }
			}
			try {
				val books = getBooksUseCase(forceRefresh)
				_uiState.update { it.copy(books = books, isLoading = false, isRefreshing = false, error = null) }
			} catch (e: Exception) {
				_uiState.update { it.copy(isLoading = false, isRefreshing = false) }
				_effect.send(BookListEffect.ShowError(e.message ?: "Unknown error occurred"))
			}
		}
	}

	private fun search(query: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			try {
				val books = searchBooksUseCase(query)
				_uiState.update { it.copy(books = books, isLoading = false, error = null) }
			} catch (e: Exception) {
				_uiState.update { it.copy(isLoading = false) }
				_effect.send(BookListEffect.ShowError(e.message ?: "Search failed"))
			}
		}
	}

	private fun clearLocal() {
		viewModelScope.launch {
			try {
				clearLocalDataUseCase()
				load(forceRefresh = false)
			} catch (e: Exception) {
				_effect.send(BookListEffect.ShowError("Failed to clear local data: ${e.message}"))
			}
		}
	}
}


