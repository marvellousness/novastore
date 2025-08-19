package nova.android.novastore.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class GetBooksFlowUseCase @Inject constructor(
	private val bookRepository: BookRepository
) {
	operator fun invoke(): Flow<List<Book>> = bookRepository.getBooksFlow()
}


