package nova.android.novastore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nova.android.novastore.data.local.entity.BookEntity

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(bookEntity: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE title LIKE :query OR author LIKE :query")
    suspend fun searchBooks(query: String): List<BookEntity>

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: String)

}