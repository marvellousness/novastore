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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepositoryImpl(
    private val remoteDataSource: RemoteBookDataSource,
    private val localDataSource: LocalBookDataSource
): BookRepository {
    
    override suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            // First, try to get books from local database (offline-first approach)
            val localBooks = localDataSource.getBooks()
            
            // If we have local data, return it immediately for fast UI response
            if (localBooks.isNotEmpty()) {
                // Fetch remote data in background for sync (non-blocking)
                fetchAndSyncRemoteData()
                localBooks
            } else {
                // If no local data, fetch from remote
                val remoteBookDtos = remoteDataSource.getBooks()
                val remoteBooks = remoteBookDtos.map { it.toDomain() }
                
                // Save to local database for future offline access
                localDataSource.saveBooks(remoteBooks)
                
                remoteBooks
            }
            
        } catch (e: Exception) {
            Log.w("BookRepository", "Remote data fetch failed, using local data", e)
            
            // Fallback to local data if available
            try {
                localDataSource.getBooks()
            } catch (localException: Exception) {
                Log.e("BookRepository", "Both remote and local data failed", localException)
                throw e // Throw original exception for better error context
            }
        }
    }

    // Private method to fetch and sync remote data without blocking UI
    private suspend fun fetchAndSyncRemoteData() {
        try {
            val remoteBookDtos = remoteDataSource.getBooks()
            val remoteBooks = remoteBookDtos.map { it.toDomain() }
            localDataSource.saveBooks(remoteBooks)
            Log.d("BookRepository", "Background sync completed successfully")
        } catch (e: Exception) {
            Log.w("BookRepository", "Background sync failed", e)
            // Don't throw here - this is background sync, shouldn't affect UI
        }
    }

    override suspend fun saveBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        localDataSource.saveBooks(books)
    }

    // New method: Get books as Flow for reactive UI updates
    override fun getBooksFlow(): Flow<List<Book>> {
        return localDataSource.getBooksFlow().map { books ->
            books.sortedBy { it.title } // Add sorting logic
        }
    }

    // New method: Get book by ID
    override suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
        try {
            localDataSource.getBookById(id)
        } catch (e: Exception) {
            Log.w("BookRepository", "Failed to get book by ID: $id", e)
            null
        }
    }

    // New method: Search books
    override suspend fun searchBooks(query: String): List<Book> = withContext(Dispatchers.IO) {
        try {
            localDataSource.searchBooks(query)
        } catch (e: Exception) {
            Log.w("BookRepository", "Failed to search books with query: $query", e)
            emptyList()
        }
    }

    // New method: Refresh books (force remote fetch)
    override suspend fun refreshBooks(): List<Book> = withContext(Dispatchers.IO) {
        try {
            val remoteBookDtos = remoteDataSource.getBooks()
            val remoteBooks = remoteBookDtos.map { it.toDomain() }
            
            // Save to local database
            localDataSource.saveBooks(remoteBooks)
            
            Log.d("BookRepository", "Books refreshed successfully")
            remoteBooks
            
        } catch (e: Exception) {
            Log.e("BookRepository", "Failed to refresh books", e)
            throw e
        }
    }

    // New method: Clear local data
    override suspend fun clearLocalData() {
        try {
            localDataSource.clearAllBooks()
            Log.d("BookRepository", "Local data cleared successfully")
        } catch (e: Exception) {
            Log.e("BookRepository", "Failed to clear local data", e)
            throw e
        }
    }

    // New method: Check if data is stale and needs refresh
    suspend fun shouldRefreshData(): Boolean = withContext(Dispatchers.IO) {
        try {
            val localBooks = localDataSource.getBooks()
            
            // If no local data, definitely need refresh
            if (localBooks.isEmpty()) return@withContext true
            
            // Check if data is older than 1 hour (3600000 milliseconds)
            val oldestBook = localBooks.minByOrNull { it.lastUpdated }
            val currentTime = System.currentTimeMillis()
            val dataAge = currentTime - (oldestBook?.lastUpdated ?: 0)
            
            dataAge > 3600000 // 1 hour in milliseconds
        } catch (e: Exception) {
            Log.w("BookRepository", "Failed to check data freshness", e)
            true // Default to refresh if we can't determine
        }
    }

    // New method: Smart get books with automatic refresh if needed
    override suspend fun getBooksSmart(): List<Book> = withContext(Dispatchers.IO) {
        if (shouldRefreshData()) {
            Log.d("BookRepository", "Data is stale, performing refresh")
            refreshBooks()
        } else {
            Log.d("BookRepository", "Data is fresh, using local data")
            getBooks()
        }
    }
}