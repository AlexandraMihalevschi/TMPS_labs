package domain.builder

import domain.models.PaperComponent
import domain.models.ResearchPaper

/**
 * Concrete component for Decorator Pattern
 */
class BasePaperComponent(private val paper: ResearchPaper) : PaperComponent {
    override fun getDisplayInfo(): String {
        return paper.getFormattedInfo()
    }

    override fun getPaper(): ResearchPaper = paper
}

