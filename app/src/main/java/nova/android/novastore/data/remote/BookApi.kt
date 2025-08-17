package nova.android.novastore.data.remote

import nova.android.novastore.data.model.Book
import retrofit2.http.GET

interface BookApi {
    @GET("books")
    suspend fun getBooks(): List<Book>
}