package domain.builder

import domain.models.PaperComponent

/**
 * Decorator that adds formatting enhancements to a paper display
 */
class FormattingDecorator(
    component: PaperComponent,
    private val formatStyle: String = "Academic"
) : PaperDecorator(component) {
    
    override fun getDisplayInfo(): String {
        val baseInfo = component.getDisplayInfo()
        val formattedHeader = when (formatStyle) {
            "Academic" -> "═══════════════════════════════════════\nACADEMIC RESEARCH PAPER\n═══════════════════════════════════════\n"
            "Modern" -> "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n📄 RESEARCH PAPER\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
            else -> "═══════════════════════════════════════\nRESEARCH PAPER\n═══════════════════════════════════════\n"
        }
        return formattedHeader + baseInfo + "\n═══════════════════════════════════════"
    }
}

