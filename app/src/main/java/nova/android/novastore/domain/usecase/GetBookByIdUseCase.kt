package nova.android.novastore.domain.usecase

import javax.inject.Inject
import nova.android.novastore.domain.model.Book
import nova.android.novastore.domain.repository.BookRepository

class GetBookByIdUseCase @Inject constructor(
	private val bookRepository: BookRepository
) {
	suspend operator fun invoke(id: String): Book? = bookRepository.getBookById(id)
}


