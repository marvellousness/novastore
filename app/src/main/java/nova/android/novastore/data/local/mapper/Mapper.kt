package nova.android.novastore.data.local.mapper

import nova.android.novastore.data.local.entity.BookEntity
import nova.android.novastore.data.model.Book

fun Book.toEntity(): BookEntity = BookEntity(id, title, author)
fun BookEntity.toDomain(): Book = Book(id, title, author)