package domain.factories

import domain.models.ResearchDatabase
import domain.models.ResearchPaper

/**
 * Facade Pattern: Provides a simplified interface to complex research database operations
 */
class ResearchFacade(private val database: ResearchDatabase) {
    
    /**
     * Simplified method to add a complete research paper with all related data
     */
    fun addCompleteResearchPaper(
        paper: ResearchPaper,
        citationIds: List<String> = emptyList(),
        reviewStatus: String = "Pending",
        reviewComments: String = ""
    ) {
        // Complex operation simplified through facade
        database.addPaper(paper)
        if (citationIds.isNotEmpty()) {
            database.addCitations(paper.id, citationIds)
        }
        if (reviewComments.isNotEmpty()) {
            database.addReview(paper.id, "$reviewStatus: $reviewComments")
        }
    }
    
    /**
     * Simplified search that searches across multiple criteria
     */
    fun searchResearch(query: String, searchType: String = "keyword"): List<ResearchPaper> {
        return when (searchType.lowercase()) {
            "keyword" -> database.searchByKeyword(query)
            "department" -> database.searchByDepartment(query)
            else -> database.getAllPapers().filter { 
                it.title.contains(query, ignoreCase = true) ||
                it.authors.any { author -> author.contains(query, ignoreCase = true) }
            }
        }
    }
    
    /**
     * Get comprehensive paper information including citations and reviews
     */
    fun getPaperDetails(paperId: String): PaperDetails? {
        val paper = database.getPaper(paperId) ?: return null
        val citations = database.getCitations(paperId)
        val review = database.getReview(paperId)
        
        return PaperDetails(
            paper = paper,
            citationCount = citations.size,
            citations = citations,
            reviewStatus = review ?: "No review available"
        )
    }
    
    /**
     * Get system overview statistics
     */
    fun getSystemOverview(): SystemOverview {
        val stats = database.getStatistics()
        val allPapers = database.getAllPapers()
        val departments = allPapers.map { it.department }.distinct()
        
        return SystemOverview(
            totalPapers = stats["Total Papers"] ?: 0,
            papersWithCitations = stats["Papers with Citations"] ?: 0,
            papersWithReviews = stats["Papers with Reviews"] ?: 0,
            departments = departments.size,
            departmentList = departments
        )
    }
}

/**
 * Data class for paper details
 */
data class PaperDetails(
    val paper: ResearchPaper,
    val citationCount: Int,
    val citations: List<String>,
    val reviewStatus: String
)

/**
 * Data class for system overview
 */
data class SystemOverview(
    val totalPapers: Int,
    val papersWithCitations: Int,
    val papersWithReviews: Int,
    val departments: Int,
    val departmentList: List<String>
)

