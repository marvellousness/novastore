package nova.android.novastore.feature.bookdetail

import nova.android.novastore.domain.model.Book

sealed interface BookDetailIntent {
	data class Load(val id: String) : BookDetailIntent
}

sealed interface BookDetailEffect {
	data class ShowError(val message: String) : BookDetailEffect
}

data class BookDetailState(
	val book: Book? = null,
	val isLoading: Boolean = false,
	val error: String? = null
) {
	val hasError: Boolean get() = error != null
}


