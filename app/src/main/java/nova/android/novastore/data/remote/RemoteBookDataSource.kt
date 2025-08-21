package nova.android.novastore.data.remote

import nova.android.novastore.data.model.BookDto
import nova.android.novastore.domain.model.ApiResult
import javax.inject.Inject

class RemoteBookDataSource @Inject constructor(private val api: BookApi) {
	suspend fun getBooks(): ApiResult<List<BookDto>> {
		return try {
			val response = api.getBooks()
			ApiResult.Success(response.data ?: emptyList())
		} catch (e: Exception) {
			ApiResult.Error(e)
		}
	}
}