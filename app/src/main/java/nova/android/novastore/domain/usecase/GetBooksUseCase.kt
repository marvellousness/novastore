package nova.android.novastore.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class GetBooksUseCase @Inject constructor(
	private val bookRepository: BookRepository
) {
	/**
	 * Returns a list of books. When [forceRefresh] is true, it fetches from remote and updates local.
	 * Otherwise it uses a smart strategy that prefers local and refreshes when stale.
	 */
	suspend operator fun invoke(forceRefresh: Boolean = false): List<Book> {
		return if (forceRefresh) {
			bookRepository.refreshBooks()
		} else {
			bookRepository.getBooksSmart()
		}
	}

	/**
	 * Reactive stream of books backed by the local database.
	 */
	fun asFlow(): Flow<List<Book>> = bookRepository.getBooksFlow()
}


