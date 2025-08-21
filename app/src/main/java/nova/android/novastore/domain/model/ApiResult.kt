package nova.android.novastore.domain.model

sealed class ApiResult<out T> {
	data class Success<out T>(val data: T) : ApiResult<T>()
	data object Loading : ApiResult<Nothing>()
	data class Error(val exception: Throwable) : ApiResult<Nothing>()
}


