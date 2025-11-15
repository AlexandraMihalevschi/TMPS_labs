package utilities

import domain.models.ResearchPaper

/**
 * Target interface for output adapters (Adapter Pattern)
 * Converts ResearchPaper objects to different output formats
 */
interface PaperOutputAdapter {
    fun convert(papers: List<ResearchPaper>): String
    fun getFormatName(): String
    fun getFileExtension(): String
}


