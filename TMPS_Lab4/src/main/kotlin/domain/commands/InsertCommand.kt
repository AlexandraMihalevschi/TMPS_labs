package domain.commands

import domain.models.TextDocument

/**
 * Command Pattern: Insert text command
 */
class InsertCommand(
    private val position: Int,
    private val text: String
) : Command {
    
    override fun execute(document: TextDocument): Boolean {
        if (position < 0 || position > document.getLength()) {
            return false
        }
        document.insertText(position, text)
        return true
    }
    
    override fun undo(document: TextDocument) {
        document.deleteText(position, text.length)
    }
    
    override fun redo(document: TextDocument) {
        document.insertText(position, text)
    }
    
    override fun toString(): String = "Insert '$text' at position $position"
}


