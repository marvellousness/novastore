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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.usecase.ClearLocalDataUseCase
import nova.android.novastore.domain.usecase.RefreshBooksUseCase
import nova.android.novastore.domain.usecase.GetBooksStreamUseCase
import nova.android.novastore.domain.usecase.SearchBooksStreamUseCase

@HiltViewModel
class BookListViewModel @Inject constructor(
	private val refreshBooksUseCase: RefreshBooksUseCase,
	private val searchBooksStreamUseCase: SearchBooksStreamUseCase,
	private val clearLocalDataUseCase: ClearLocalDataUseCase,
	getBooksStreamUseCase: GetBooksStreamUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookListState())
	val uiState: StateFlow<BookListState> = _uiState.asStateFlow()

	// Query input for reactive search
	private val query = MutableStateFlow("")

	// Base books stream
	private val allBooksFlow: Flow<List<Book>> = getBooksStreamUseCase()

	// Reactive search stream consumed by UI
	val booksFlow: Flow<List<Book>> = query
		.debounce(300)
		.map { it.trim() }
		.distinctUntilChanged()
		.flatMapLatest { q -> if (q.isEmpty()) allBooksFlow else searchBooksStreamUseCase(q) }

	// One-off effects
	private val _effect = Channel<BookListEffect>(Channel.BUFFERED)
	val effect: Flow<BookListEffect> = _effect.receiveAsFlow()

	init {
		dispatch(BookListIntent.Load())
	}

	fun dispatch(intent: BookListIntent) {
		when (intent) {
			is BookListIntent.Load -> load(forceRefresh = intent.forceRefresh)
			is BookListIntent.Search -> query.value = intent.query
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
				if (forceRefresh) refreshBooksUseCase()
				_uiState.update { it.copy(isLoading = false, isRefreshing = false, error = null) }
			} catch (e: Exception) {
				_uiState.update { it.copy(isLoading = false, isRefreshing = false) }
				_effect.send(BookListEffect.ShowError(e.message ?: "Unknown error occurred"))
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


