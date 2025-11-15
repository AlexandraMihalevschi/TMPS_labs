package domain.models

/**
 * Target interface for research data sources (Adapter Pattern)
 */
interface ResearchDataSource {
    fun getData(): List<ResearchData>
    fun getSourceType(): String
}

