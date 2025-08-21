package nova.android.novastore.domain.repository

import nova.android.novastore.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
	// Reactive reads
	fun getBooksFlow(): Flow<List<Book>>
	fun observeBookById(id: String): Flow<Book?>
	fun searchBooksFlow(query: String): Flow<List<Book>>

	// Commands/one-off operations
	suspend fun refreshBooks(): List<Book>
	suspend fun getBookById(id: String): Book?
	suspend fun searchBooks(query: String): List<Book>
	suspend fun saveBooks(books: List<Book>)
	suspend fun clearLocalData()
}