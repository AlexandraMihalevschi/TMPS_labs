package domain.models

/**
 * Represents research data that can come from various sources
 */
data class ResearchData(
    val paperId: String,
    val title: String,
    val author: String,
    val year: String,
    val topic: String
)

