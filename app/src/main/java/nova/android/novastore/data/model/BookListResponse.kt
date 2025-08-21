package nova.android.novastore.data.model

data class BookListResponse(
    override val data: List<BookDto>? = null
) : ApiEnvelope<List<BookDto>>(0, null, data)
