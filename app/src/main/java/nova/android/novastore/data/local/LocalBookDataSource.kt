package nova.android.novastore.data.local

import kotlinx.coroutines.flow.first
import nova.android.novastore.data.local.dao.BookDao
import nova.android.novastore.data.local.mapper.toDomain
import nova.android.novastore.data.local.mapper.toEntity
import nova.android.novastore.data.model.Book
import javax.inject.Inject

class LocalBookDataSource @Inject constructor(private val bookDao: BookDao) {

     suspend fun getBooks(): List<Book> =
        bookDao.getAllBooks().first().map { it.toDomain() }

     suspend fun saveBooks(books: List<Book>) {
        val bookEntities = books.map { it.toEntity() }
        bookDao.insertBooks(bookEntities)
    }
}