package domain.builder

import domain.models.PaperComponent
import domain.models.ResearchDatabase

/**
 * Decorator that adds citation information to a paper
 */
class CitationDecorator(
    component: PaperComponent,
    private val database: ResearchDatabase
) : PaperDecorator(component) {
    
    override fun getDisplayInfo(): String {
        val baseInfo = component.getDisplayInfo()
        val citations = database.getCitations(getPaper().id)
        val citationText = if (citations.isNotEmpty()) {
            "\nCitations: ${citations.size} reference(s)"
        } else {
            "\nCitations: None"
        }
        return baseInfo + citationText
    }
}

