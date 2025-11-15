package domain.builder

import domain.models.PaperComponent

/**
 * Decorator that adds peer review status to a paper
 */
class PeerReviewDecorator(
    component: PaperComponent,
    private val reviewStatus: String,
    private val reviewerComments: String
) : PaperDecorator(component) {
    
    override fun getDisplayInfo(): String {
        val baseInfo = component.getDisplayInfo()
        val reviewText = """
            
            Peer Review Status: $reviewStatus
            Reviewer Comments: $reviewerComments
        """.trimIndent()
        return baseInfo + reviewText
    }
}

