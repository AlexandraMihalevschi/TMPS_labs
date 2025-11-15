package domain.builder

import domain.models.PaperComponent

/**
 * Base decorator class for Decorator Pattern
 */
abstract class PaperDecorator(protected val component: PaperComponent) : PaperComponent {
    override fun getPaper() = component.getPaper()
}

