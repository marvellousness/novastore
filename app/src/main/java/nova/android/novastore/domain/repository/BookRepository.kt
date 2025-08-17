package nova.android.novastore.domain.repository

import nova.android.novastore.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun saveBooks(books: List<Book>)
    
    // New methods for better data management
    suspend fun refreshBooks(): List<Book>
    fun getBooksFlow(): Flow<List<Book>>
    suspend fun getBookById(id: String): Book?
    suspend fun searchBooks(query: String): List<Book>
    suspend fun clearLocalData()
    suspend fun getBooksSmart(): List<Book>
}