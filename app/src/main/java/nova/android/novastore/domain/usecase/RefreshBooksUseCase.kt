package nova.android.novastore.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import nova.android.novastore.di.IoDispatcher
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class RefreshBooksUseCase @Inject constructor(
	private val bookRepository: BookRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
	suspend operator fun invoke(): List<Book> = withContext(ioDispatcher) {
		bookRepository.refreshBooks().sortedBy { it.title }
	}
}


