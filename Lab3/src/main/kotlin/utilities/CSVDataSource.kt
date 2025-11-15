package utilities

import domain.models.ResearchData
import domain.models.ResearchDataSource

/**
 * CSV data source implementation
 */
class CSVDataSource(private val csvContent: String) : ResearchDataSource {
    override fun getData(): List<ResearchData> {
        val lines = csvContent.trim().split("\n")
        val data = mutableListOf<ResearchData>()
        
        // Skip header line
        for (i in 1 until lines.size) {
            val parts = lines[i].split(",")
            if (parts.size >= 5) {
                data.add(
                    ResearchData(
                        paperId = parts[0].trim(),
                        title = parts[1].trim(),
                        author = parts[2].trim(),
                        year = parts[3].trim(),
                        topic = parts[4].trim()
                    )
                )
            }
        }
        return data
    }

    override fun getSourceType(): String = "CSV"
}

