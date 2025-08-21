package nova.android.novastore.data.repository

import nova.android.novastore.data.local.LocalBookDataSource
import nova.android.novastore.domain.model.Book
import nova.android.novastore.data.remote.RemoteBookDataSource
import nova.android.novastore.domain.repository.BookRepository
import nova.android.novastore.data.local.mapper.toDomain
import android.util.Log
import kotlinx.coroutines.flow.Flow

class BookRepositoryImpl(
    private val remoteDataSource: RemoteBookDataSource,
    private val localDataSource: LocalBookDataSource
): BookRepository {

    // Reactive reads
    override fun getBooksFlow(): Flow<List<Book>> = localDataSource.getBooksFlow()

    override fun observeBookById(id: String): Flow<Book?> =
        localDataSource.observeBookById(id)

    override fun searchBooksFlow(query: String): Flow<List<Book>> =
        localDataSource.searchBooksFlow(query)

    // Commands/one-off operations
    override suspend fun refreshBooks(): List<Book> {
        return try {
            val remoteBookDtos = remoteDataSource.getBooks()
            val remoteBooks = remoteBookDtos.map { it.toDomain() }
            localDataSource.saveBooks(remoteBooks)
            Log.d("BookRepository", "Books refreshed successfully")
            remoteBooks
        } catch (e: Exception) {
            Log.e("BookRepository", "Failed to refresh books", e)
            throw e
        }
    }

    override suspend fun getBookById(id: String): Book? {
        return try {
            localDataSource.getBookById(id)
        } catch (e: Exception) {
            Log.w("BookRepository", "Failed to get book by ID: $id", e)
            null
        }
    }

    override suspend fun searchBooks(query: String): List<Book> {
        return try {
            localDataSource.searchBooks(query)
        } catch (e: Exception) {
            Log.w("BookRepository", "Failed to search books with query: $query", e)
            emptyList()
        }
    }

    override suspend fun saveBooks(books: List<Book>) {
        localDataSource.saveBooks(books)
    }

    override suspend fun clearLocalData() {
        try {
            localDataSource.clearAllBooks()
            Log.d("BookRepository", "Local data cleared successfully")
        } catch (e: Exception) {
            Log.e("BookRepository", "Failed to clear local data", e)
            throw e
        }
    }
}