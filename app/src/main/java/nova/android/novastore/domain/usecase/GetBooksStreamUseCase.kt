package nova.android.novastore.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import nova.android.novastore.di.IoDispatcher
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class GetBooksStreamUseCase @Inject constructor(
	private val bookRepository: BookRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
	operator fun invoke(): Flow<List<Book>> =
		bookRepository
			.getBooksFlow()
			.map { books -> books.sortedBy { it.title } }
			.flowOn(ioDispatcher)
}


