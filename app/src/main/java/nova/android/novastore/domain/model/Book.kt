package nova.android.novastore.domain.model

data class Book(
    val id: Int,
    val title: String? = "",
    val author: String? = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // Domain logic methods
   fun isRecentlyUpdated(): Boolean = System.currentTimeMillis() - lastUpdated < 24 * 60 * 60 * 1000
}