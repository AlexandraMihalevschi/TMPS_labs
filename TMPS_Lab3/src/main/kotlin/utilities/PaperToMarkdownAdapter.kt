package utilities

import domain.models.ResearchPaper

/**
 * Adapter that converts ResearchPaper objects to Markdown format
 */
class PaperToMarkdownAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        if (papers.isEmpty()) return ""
        
        return papers.mapIndexed { index, paper ->
            """
            ## ${index + 1}. ${paper.title}

            **Paper ID:** ${paper.id}  
            **Authors:** ${paper.authors.joinToString(", ")}  
            **Department:** ${paper.department}  
            **Year:** ${paper.publicationDate}  
            **Keywords:** ${paper.keywords.joinToString(", ")}

            ### Abstract

            ${paper.abstract}

            ---
            """.trimIndent()
        }.joinToString("\n\n")
    }
    
    override fun getFormatName(): String = "Markdown"
    
    override fun getFileExtension(): String = "md"
}


