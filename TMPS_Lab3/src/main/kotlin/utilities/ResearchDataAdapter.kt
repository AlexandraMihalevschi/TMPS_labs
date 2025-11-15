package utilities

import domain.models.ResearchData
import domain.models.ResearchDataSource
import domain.models.ResearchPaper

/**
 * Adapter Pattern: Adapts ResearchData to ResearchPaper
 * This allows different data sources to be converted to a common format
 */
class ResearchDataAdapter(private val dataSource: ResearchDataSource) {
    fun adaptToResearchPapers(): List<ResearchPaper> {
        val data = dataSource.getData()
        return data.map { dataItem ->
            ResearchPaper(
                id = dataItem.paperId,
                title = dataItem.title,
                authors = listOf(dataItem.author),
                abstract = "Abstract for ${dataItem.title}",
                keywords = listOf(dataItem.topic),
                publicationDate = dataItem.year,
                department = "Computer Science" // Default department
            )
        }
    }
    
    fun getSourceType(): String = dataSource.getSourceType()
}

