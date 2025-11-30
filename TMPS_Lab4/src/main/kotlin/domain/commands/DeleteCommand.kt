package domain.commands

import domain.models.TextDocument

/**
 * Command Pattern: Delete text command
 */
class DeleteCommand(
    private val position: Int,
    private val length: Int
) : Command {
    private var deletedText: String = ""
    private var deletedFormats: Map<Int, domain.models.TextFormat> = mutableMapOf()
    
    override fun execute(document: TextDocument): Boolean {
        if (position < 0 || position + length > document.getLength()) {
            return false
        }
        deletedText = document.getText().substring(position, position + length)
        deletedFormats = document.formatting.filterKeys { it in position until position + length }
            .mapKeys { it.key - position }
            .toMap()
        document.deleteText(position, length)
        return true
    }
    
    override fun undo(document: TextDocument) {
        document.insertText(position, deletedText)
        deletedFormats.forEach { (offset, format) ->
            document.applyFormat(position + offset, 1, format)
        }
    }
    
    override fun redo(document: TextDocument) {
        document.deleteText(position, deletedText.length)
    }
    
    override fun toString(): String = "Delete $length characters from position $position"
}


