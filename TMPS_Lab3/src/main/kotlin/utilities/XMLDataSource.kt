package utilities

import domain.models.ResearchData
import domain.models.ResearchDataSource

/**
 * XML data source implementation
 */
class XMLDataSource(private val xmlContent: String) : ResearchDataSource {
    override fun getData(): List<ResearchData> {
        val data = mutableListOf<ResearchData>()
        
        // Simple XML parsing (in production, use a proper XML parser)
        val records = xmlContent.split("<record>")
        
        for (i in 1 until records.size) {
            val record = records[i]
            val paperId = extractTag(record, "paperId")
            val title = extractTag(record, "title")
            val author = extractTag(record, "author")
            val year = extractTag(record, "year")
            val topic = extractTag(record, "topic")
            
            if (paperId.isNotEmpty()) {
                data.add(ResearchData(paperId, title, author, year, topic))
            }
        }
        return data
    }

    private fun extractTag(record: String, tagName: String): String {
        val startTag = "<$tagName>"
        val endTag = "</$tagName>"
        val startIndex = record.indexOf(startTag)
        val endIndex = record.indexOf(endTag)
        
        return if (startIndex != -1 && endIndex != -1) {
            record.substring(startIndex + startTag.length, endIndex).trim()
        } else {
            ""
        }
    }

    override fun getSourceType(): String = "XML"
}

