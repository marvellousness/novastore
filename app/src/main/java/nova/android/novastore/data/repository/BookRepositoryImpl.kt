package nova.android.novastore.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nova.android.novastore.data.local.LocalBookDataSource
import nova.android.novastore.data.local.dao.BookDao
import nova.android.novastore.data.local.entity.BookEntity
import nova.android.novastore.domain.model.Book
import nova.android.novastore.data.remote.BookApi
import nova.android.novastore.data.remote.RemoteBookDataSource
import nova.android.novastore.domain.repository.BookRepository
import nova.android.novastore.data.local.mapper.toEntity
import nova.android.novastore.data.local.mapper.toDomain
import android.util.Log

class BookRepositoryImpl(
    private val remoteDataSource: RemoteBookDataSource,
    private val localDataSource: LocalBookDataSource
): BookRepository {
    override suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            // Try to get books from remote source first
            val remoteBookDtos = remoteDataSource.getBooks()
            // Convert DTOs to domain models
            val remoteBooks = remoteBookDtos.map { it.toDomain() }
            // Save to local database (LocalBookDataSource handles the conversion to entities)
            localDataSource.saveBooks(remoteBooks)
            remoteBooks
        } catch (e: Exception) {
            // Log the error for debugging
            Log.w("BookRepository", "Remote data fetch failed, falling back to local data", e)
            // If remote fails, get from local database
            localDataSource.getBooks()
        }
    }

    override suspend fun saveBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        localDataSource.saveBooks(books)
    }
}