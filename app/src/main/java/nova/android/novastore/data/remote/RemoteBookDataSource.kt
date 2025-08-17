package nova.android.novastore.data.remote

import nova.android.novastore.data.model.BookDto
import javax.inject.Inject

class RemoteBookDataSource  @Inject constructor(private val api: BookApi)  {
 suspend fun getBooks(): List<BookDto> = api.getBooks()
}