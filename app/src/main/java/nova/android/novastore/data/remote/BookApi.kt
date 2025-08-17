package nova.android.novastore.data.remote

import nova.android.novastore.data.model.BookDto
import retrofit2.http.GET

interface BookApi {
    @GET("posts")
    suspend fun getBooks(): List<BookDto>
}