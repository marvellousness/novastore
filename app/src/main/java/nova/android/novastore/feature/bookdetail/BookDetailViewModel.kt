package nova.android.novastore.feature.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nova.android.novastore.domain.usecase.GetBookStreamUseCase

@HiltViewModel
class BookDetailViewModel @Inject constructor(
	private val getBookStreamUseCase: GetBookStreamUseCase
) : ViewModel() {

	private val _uiState = MutableStateFlow(BookDetailState())
	val uiState: StateFlow<BookDetailState> = _uiState.asStateFlow()

	private val _effect = Channel<BookDetailEffect>(Channel.BUFFERED)
	val effect = _effect.receiveAsFlow()

	fun dispatch(intent: BookDetailIntent) {
		when (intent) {
			is BookDetailIntent.Load -> load(intent.id)
		}
	}

	private fun load(id: String) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			getBookStreamUseCase(id).collectLatest { book ->
				_uiState.update { it.copy(book = book, isLoading = false) }
				if (book == null) {
					_effect.send(BookDetailEffect.ShowError("Book not found"))
				}
			}
		}
	}
}


