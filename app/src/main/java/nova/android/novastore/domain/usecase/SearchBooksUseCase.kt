package nova.android.novastore.domain.usecase

import javax.inject.Inject
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class SearchBooksUseCase @Inject constructor(
	private val bookRepository: BookRepository
) {
	suspend operator fun invoke(query: String): List<Book> = bookRepository.searchBooks(query)
}


