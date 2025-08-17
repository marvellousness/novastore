package nova.android.novastore.ui.state

import nova.android.novastore.domain.model.Book

sealed class BookUiState {
    object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
    object Empty : BookUiState()
}

data class BookListState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    val isEmpty: Boolean get() = books.isEmpty() && !isLoading && error == null
    val hasError: Boolean get() = error != null
}
