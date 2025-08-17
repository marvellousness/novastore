package nova.android.novastore.data.model
import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val id: Int,
    val title: String?,
    val author: String?,
)