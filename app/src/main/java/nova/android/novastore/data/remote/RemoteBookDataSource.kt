package nova.android.novastore.data.remote

import nova.android.novastore.data.model.Book
import javax.inject.Inject

class RemoteBookDataSource  @Inject constructor(private val api: BookApi)  {
 suspend fun getBooks(): List<Book> = api.getBooks()
}