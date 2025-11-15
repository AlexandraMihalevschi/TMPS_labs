package utilities

import domain.models.ResearchData
import domain.models.ResearchDataSource

/**
 * JSON data source implementation
 */
class JSONDataSource(private val jsonContent: String) : ResearchDataSource {
    override fun getData(): List<ResearchData> {
        val data = mutableListOf<ResearchData>()
        
        // Simple JSON parsing (in production, use a proper JSON library)
        val entries = jsonContent.split("},")
        for (entry in entries) {
            val cleanEntry = entry.replace("{", "").replace("}", "").replace("\"", "").trim()
            val fields = cleanEntry.split(",")
            
            var paperId = ""
            var title = ""
            var author = ""
            var year = ""
            var topic = ""
            
            for (field in fields) {
                val keyValue = field.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim()
                    val value = keyValue[1].trim()
                    when (key) {
                        "paperId" -> paperId = value
                        "title" -> title = value
                        "author" -> author = value
                        "year" -> year = value
                        "topic" -> topic = value
                    }
                }
            }
            
            if (paperId.isNotEmpty()) {
                data.add(ResearchData(paperId, title, author, year, topic))
            }
        }
        return data
    }

    override fun getSourceType(): String = "JSON"
}

