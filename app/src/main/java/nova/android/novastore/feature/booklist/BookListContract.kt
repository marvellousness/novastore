package nova.android.novastore.feature.booklist

import nova.android.novastore.domain.model.Book

// Intents represent user actions or events
sealed interface BookListIntent {
	data class Load(val forceRefresh: Boolean = false) : BookListIntent
	data class Search(val query: String) : BookListIntent
	data object Retry : BookListIntent
	data object ClearLocal : BookListIntent
}

// One-off side effects
sealed interface BookListEffect {
	data class ShowError(val message: String) : BookListEffect
}

// UI State for the screen
data class BookListState(
	val books: List<Book> = emptyList(),
	val isLoading: Boolean = false,
	val error: String? = null,
	val isRefreshing: Boolean = false
) {
	val isEmpty: Boolean get() = books.isEmpty() && !isLoading && error == null
	val hasError: Boolean get() = error != null
}


