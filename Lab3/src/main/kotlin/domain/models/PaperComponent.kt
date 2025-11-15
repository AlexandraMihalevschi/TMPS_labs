package domain.models

/**
 * Component interface for Decorator Pattern
 */
interface PaperComponent {
    fun getDisplayInfo(): String
    fun getPaper(): ResearchPaper
}

