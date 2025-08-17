package nova.android.novastore.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // Domain logic methods
    fun isRecentlyUpdated(): Boolean = System.currentTimeMillis() - lastUpdated < 24 * 60 * 60 * 1000
    
    fun getDisplayTitle(): String = if (title.isBlank()) "Untitled" else title
    
    fun getAuthorDisplay(): String = if (author.isBlank()) "Unknown Author" else author
}