package nova.android.novastore.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import nova.android.novastore.data.local.dao.BookDao
import nova.android.novastore.data.local.mapper.toDomain
import nova.android.novastore.data.local.mapper.toEntity
import nova.android.novastore.domain.model.Book
import javax.inject.Inject

class LocalBookDataSource @Inject constructor(private val bookDao: BookDao) {

     suspend fun getBooks(): List<Book> =
        bookDao.getAllBooks().first().map { it.toDomain() }

     suspend fun saveBooks(books: List<Book>) {
        val bookEntities = books.map { it.toEntity() }
        bookDao.insertBooks(bookEntities)
    }

    // New method: Get books as Flow for reactive updates
    fun getBooksFlow(): Flow<List<Book>> =
        bookDao.getAllBooks().map { entities ->
            entities.map { it.toDomain() }
        }

    // New method: Get book by ID
    suspend fun getBookById(id: String): Book? {
        return try {
            bookDao.getBookById(id)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    fun observeBookById(id: String): Flow<Book?> =
        bookDao.observeBookById(id).map { it?.toDomain() }

    // New method: Search books
    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val searchQuery = "%$query%"
            bookDao.searchBooks(searchQuery).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun searchBooksFlow(query: String): Flow<List<Book>> =
        bookDao.searchBooksFlow("%$query%").map { entities ->
            entities.map { it.toDomain() }
        }

    // New method: Clear all books
    suspend fun clearAllBooks() {
        try {
            bookDao.deleteAllBooks()
        } catch (e: Exception) {
            throw e
        }
    }
}