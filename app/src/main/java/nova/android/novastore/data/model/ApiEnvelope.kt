package nova.android.novastore.data.model

import kotlinx.serialization.Serializable

@Serializable
open class ApiEnvelope<T>(
	open val responseCode: Int,
	open val message: String?,
	open val data: T?
)


