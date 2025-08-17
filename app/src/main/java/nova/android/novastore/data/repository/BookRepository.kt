package nova.android.novastore.data.repository

import nova.android.novastore.data.model.Book

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun saveBooks(books: List<Book>)
}