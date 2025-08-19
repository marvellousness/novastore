package nova.android.novastore.domain.usecase

import javax.inject.Inject
import nova.android.novastore.domain.repository.BookRepository

class ClearLocalDataUseCase @Inject constructor(
	private val bookRepository: BookRepository
) {
	suspend operator fun invoke() {
		bookRepository.clearLocalData()
	}
}


