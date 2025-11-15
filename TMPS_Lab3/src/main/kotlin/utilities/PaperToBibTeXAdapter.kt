package utilities

import domain.models.ResearchPaper

/**
 * Adapter that converts ResearchPaper objects to BibTeX format
 */
class PaperToBibTeXAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        if (papers.isEmpty()) return ""
        
        return papers.mapIndexed { index, paper ->
            val key = "${paper.id.lowercase().replace(" ", "")}${paper.publicationDate}"
            val authors = paper.authors.joinToString(" and ")
            val keywords = paper.keywords.joinToString(", ")
            
            """
            @article{$key,
                title = {${paper.title}},
                author = {$authors},
                year = {${paper.publicationDate}},
                journal = {Research Paper},
                keywords = {$keywords},
                abstract = {${paper.abstract.replace("\n", " ")}},
                department = {${paper.department}}
            }
            """.trimIndent()
        }.joinToString("\n\n")
    }
    
    override fun getFormatName(): String = "BibTeX"
    
    override fun getFileExtension(): String = "bib"
}


