package domain.models

/**
 * Represents a research paper in the university research system
 */
data class ResearchPaper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val abstract: String,
    val keywords: List<String>,
    val publicationDate: String,
    val department: String
) {
    fun getFormattedInfo(): String {
        return """
            Title: $title
            Authors: ${authors.joinToString(", ")}
            Department: $department
            Publication Date: $publicationDate
            Keywords: ${keywords.joinToString(", ")}
        """.trimIndent()
    }
}

