package utilities

import domain.models.ResearchPaper

/**
 * Adapter that converts ResearchPaper objects to XML format
 */
class PaperToXMLAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        if (papers.isEmpty()) return "<papers></papers>"
        
        val xmlRecords = papers.map { paper ->
            """
            <paper>
                <paperId>${paper.id}</paperId>
                <title>${escapeXml(paper.title)}</title>
                <authors>
                    ${paper.authors.joinToString("\n                    ") { "<author>${escapeXml(it)}</author>" }}
                </authors>
                <department>${escapeXml(paper.department)}</department>
                <year>${paper.publicationDate}</year>
                <keywords>
                    ${paper.keywords.joinToString("\n                    ") { "<keyword>${escapeXml(it)}</keyword>" }}
                </keywords>
                <abstract>${escapeXml(paper.abstract)}</abstract>
            </paper>
            """.trimIndent()
        }
        
        return "<papers>\n${xmlRecords.joinToString("\n").prependIndent("  ")}\n</papers>"
    }
    
    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    override fun getFormatName(): String = "XML"
    
    override fun getFileExtension(): String = "xml"
}


