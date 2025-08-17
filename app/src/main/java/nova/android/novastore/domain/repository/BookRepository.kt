package nova.android.novastore.domain.repository

import nova.android.novastore.domain.model.Book

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun saveBooks(books: List<Book>)
}