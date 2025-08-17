package nova.android.novastore.feature.bookslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nova.android.novastore.domain.repository.BookRepository
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookListState())
    val uiState: StateFlow<BookListState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Use smart data fetching that checks freshness
                val books = bookRepository.getBooksSmart()
                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun refreshBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                val books = bookRepository.refreshBooks()
                _uiState.update {
                    it.copy(
                        books = books,
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun retry() {
        loadBooks()
    }

    // New method: Search books
    fun searchBooks(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val books = bookRepository.searchBooks(query)
                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    // New method: Clear local data
    fun clearLocalData() {
        viewModelScope.launch {
            try {
                bookRepository.clearLocalData()
                // Reload books after clearing
                loadBooks()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to clear local data: ${e.message}")
                }
            }
        }
    }
}