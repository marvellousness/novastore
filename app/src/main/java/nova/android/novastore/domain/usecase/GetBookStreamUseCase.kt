package nova.android.novastore.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import nova.android.novastore.di.IoDispatcher
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class GetBookStreamUseCase @Inject constructor(
	private val bookRepository: BookRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
	operator fun invoke(id: String): Flow<Book?> =
		bookRepository.observeBookById(id).flowOn(ioDispatcher)
}


