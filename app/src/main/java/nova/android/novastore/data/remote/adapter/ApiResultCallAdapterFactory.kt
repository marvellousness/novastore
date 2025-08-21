package nova.android.novastore.data.remote.adapter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import nova.android.novastore.data.model.ApiEnvelope
import nova.android.novastore.domain.model.ApiResult
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

class ApiResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != ApiResult::class.java) return null

        require(returnType is ParameterizedType) {
            "ApiResult must be parameterized as ApiResult<Foo>"
        }
        val successType = getParameterUpperBound(0, returnType)
        return ApiResultCallAdapter<Any>(successType)
    }

    private class ApiResultCall<T : Any>(
        private val delegate: Call<ApiEnvelope<T>>,
        private val successType: Type
    ) : Call<ApiResult<T>> {

        override fun enqueue(callback: retrofit2.Callback<ApiResult<T>>) {
            delegate.enqueue(object : retrofit2.Callback<ApiEnvelope<T>> {
                override fun onResponse(call: Call<ApiEnvelope<T>>, response: Response<ApiEnvelope<T>>) {
                    val result = toApiResult(response)
                    callback.onResponse(this@ApiResultCall, Response.success(result))
                }

                override fun onFailure(call: Call<ApiEnvelope<T>>, t: Throwable) {
                    callback.onResponse(this@ApiResultCall, Response.success(ApiResult.Error(t)))
                }
            })
        }

        override fun isExecuted(): Boolean = delegate.isExecuted

        override fun clone(): Call<ApiResult<T>> = ApiResultCall(delegate.clone(), successType)

        override fun isCanceled(): Boolean = delegate.isCanceled

        override fun cancel() = delegate.cancel()

        override fun execute(): Response<ApiResult<T>> {
            return try {
                val response = delegate.execute()
                Response.success(toApiResult(response))
            } catch (t: Throwable) {
                Response.success(ApiResult.Error(t))
            }
        }

        override fun request(): Request = delegate.request()

        override fun timeout(): Timeout = delegate.timeout()

        private fun toApiResult(response: Response<ApiEnvelope<T>>): ApiResult<T> {
            if (!response.isSuccessful) return ApiResult.Error(HttpException(response))
            val envelope = response.body() ?: return ApiResult.Error(IllegalStateException("Empty body"))
            return if (envelope.responseCode == 0) {
                val data = envelope.data
                if (data == null) ApiResult.Error(IllegalStateException("Envelope has null data"))
                else ApiResult.Success(data)
            } else {
                ApiResult.Error(IllegalStateException(envelope.message ?: "responseCode=${envelope.responseCode}"))
            }
        }
    }

    private class ApiResultCallAdapter<T : Any>(
        private val successType: Type
    ) : CallAdapter<ApiEnvelope<T>, Call<ApiResult<T>>> {

        override fun responseType(): Type = parameterized(ApiEnvelope::class.java, successType)

        override fun adapt(call: Call<ApiEnvelope<T>>): Call<ApiResult<T>> = ApiResultCall(call, successType)
    }
}

private fun parameterized(raw: Class<*>, vararg args: Type): ParameterizedType = object : ParameterizedType {
    override fun getRawType(): Type = raw
    override fun getOwnerType(): Type? = null
    override fun getActualTypeArguments(): Array<out Type> = args
}


