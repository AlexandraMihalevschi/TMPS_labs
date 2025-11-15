package utilities

import domain.models.ResearchPaper

/**
 * Adapter that converts ResearchPaper objects to CSV format
 */
class PaperToCSVAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        if (papers.isEmpty()) return ""
        
        val header = "Paper ID,Title,Authors,Department,Year,Keywords,Abstract"
        val rows = papers.map { paper ->
            val authors = paper.authors.joinToString("; ")
            val keywords = paper.keywords.joinToString("; ")
            val abstract = paper.abstract.replace("\n", " ").replace(",", ";")
            "\"${paper.id}\",\"${paper.title}\",\"$authors\",\"${paper.department}\",\"${paper.publicationDate}\",\"$keywords\",\"$abstract\""
        }
        
        return (listOf(header) + rows).joinToString("\n")
    }
    
    override fun getFormatName(): String = "CSV"
    
    override fun getFileExtension(): String = "csv"
}


