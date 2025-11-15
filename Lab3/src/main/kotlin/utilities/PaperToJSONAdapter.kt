package utilities

import domain.models.ResearchPaper

/**
 * Adapter that converts ResearchPaper objects to JSON format
 */
class PaperToJSONAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        if (papers.isEmpty()) return "[]"
        
        val jsonObjects = papers.map { paper ->
            """
            {
                "paperId": "${paper.id}",
                "title": "${paper.title.replace("\"", "\\\"")}",
                "authors": [${paper.authors.joinToString(", ") { "\"$it\"" }}],
                "department": "${paper.department}",
                "year": "${paper.publicationDate}",
                "keywords": [${paper.keywords.joinToString(", ") { "\"$it\"" }}],
                "abstract": "${paper.abstract.replace("\"", "\\\"").replace("\n", "\\n")}"
            }
            """.trimIndent()
        }
        
        return "[\n${jsonObjects.joinToString(",\n").prependIndent("  ")}\n]"
    }
    
    override fun getFormatName(): String = "JSON"
    
    override fun getFileExtension(): String = "json"
}


