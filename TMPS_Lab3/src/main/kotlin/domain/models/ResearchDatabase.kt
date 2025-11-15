package domain.models

/**
 * Complex research database system with multiple subsystems
 */
class ResearchDatabase {
    private val papers = mutableListOf<ResearchPaper>()
    private val citations = mutableMapOf<String, List<String>>()
    private val reviews = mutableMapOf<String, String>()

    fun addPaper(paper: ResearchPaper) {
        papers.add(paper)
    }

    fun getPaper(id: String): ResearchPaper? {
        return papers.find { it.id == id }
    }

    fun getAllPapers(): List<ResearchPaper> {
        return papers.toList()
    }

    fun searchByKeyword(keyword: String): List<ResearchPaper> {
        val lowerKeyword = keyword.lowercase()
        return papers.filter { 
            it.keywords.any { k -> k.lowercase().contains(lowerKeyword) } || 
            it.title.lowercase().contains(lowerKeyword)
        }
    }

    fun searchByDepartment(department: String): List<ResearchPaper> {
        val lowerDepartment = department.lowercase()
        return papers.filter { it.department.lowercase() == lowerDepartment }
    }

    fun addCitations(paperId: String, citationIds: List<String>) {
        citations[paperId] = citationIds
    }

    fun getCitations(paperId: String): List<String> {
        return citations[paperId] ?: emptyList()
    }

    fun addReview(paperId: String, review: String) {
        reviews[paperId] = review
    }

    fun getReview(paperId: String): String? {
        return reviews[paperId]
    }

    fun getStatistics(): Map<String, Int> {
        return mapOf(
            "Total Papers" to papers.size,
            "Papers with Citations" to citations.size,
            "Papers with Reviews" to reviews.size
        )
    }
}

