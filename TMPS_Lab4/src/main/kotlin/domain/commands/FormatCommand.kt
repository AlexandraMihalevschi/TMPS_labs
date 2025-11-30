package domain.commands

import domain.models.TextDocument
import domain.models.TextFormat

/**
 * Command Pattern: Format text command
 */
class FormatCommand(
    private val position: Int,
    private val length: Int,
    private val format: TextFormat
) : Command {
    private val previousFormats: MutableMap<Int, TextFormat?> = mutableMapOf()
    
    override fun execute(document: TextDocument): Boolean {
        if (position < 0 || position + length > document.getLength()) {
            return false
        }
        
        // Save previous formats
        for (i in position until position + length) {
            previousFormats[i] = document.formatting[i]
        }
        
        document.applyFormat(position, length, format)
        return true
    }
    
    override fun undo(document: TextDocument) {
        previousFormats.forEach { (pos, format) ->
            if (format != null) {
                document.applyFormat(pos, 1, format)
            } else {
                document.removeFormat(pos, 1)
            }
        }
    }
    
    override fun redo(document: TextDocument) {
        document.applyFormat(position, length, format)
    }
    
    override fun toString(): String {
        val formatDesc = buildString {
            if (format.isBold) append("bold")
            if (format.isItalic) {
                if (isNotEmpty()) append("+")
                append("italic")
            }
        }
        return "Format $length characters at position $position as $formatDesc"
    }
}


