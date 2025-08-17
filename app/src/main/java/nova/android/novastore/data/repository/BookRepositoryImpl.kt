package nova.android.novastore.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nova.android.novastore.data.local.LocalBookDataSource
import nova.android.novastore.data.local.dao.BookDao
import nova.android.novastore.data.local.entity.BookEntity
import nova.android.novastore.data.model.Book
import nova.android.novastore.data.remote.BookApi
import nova.android.novastore.data.remote.RemoteBookDataSource

class BookRepositoryImpl(
    private val remoteDataSource: RemoteBookDataSource,
    private val localDataSource: LocalBookDataSource
): BookRepository  {
    override suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            // Try to get books from remote source first
            val remoteBooks = remoteDataSource.getBooks()
            // Save to local database
            localDataSource.saveBooks(remoteBooks)
            remoteBooks
        } catch (e: Exception) {
            // If remote fails, get from local database
            localDataSource.getBooks()
        }
    }

    override suspend fun saveBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        localDataSource.saveBooks(books)
    }
}