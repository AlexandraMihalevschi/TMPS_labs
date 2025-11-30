package domain.models

/**
 * Represents a text document with content and formatting information
 */
data class TextDocument(
    val content: StringBuilder = StringBuilder(),
    val formatting: MutableMap<Int, TextFormat> = mutableMapOf()
) {
    fun getText(): String = content.toString()
    
    fun getLength(): Int = content.length
    
    fun insertText(position: Int, text: String) {
        content.insert(position, text)
        shiftFormatting(position, text.length)
    }
    
    fun deleteText(position: Int, length: Int) {
        content.delete(position, position + length)
        shiftFormatting(position, -length)
    }
    
    fun applyFormat(position: Int, length: Int, format: TextFormat) {
        for (i in position until position + length) {
            formatting[i] = format
        }
    }
    
    fun removeFormat(position: Int, length: Int) {
        for (i in position until position + length) {
            formatting.remove(i)
        }
    }
    
    private fun shiftFormatting(fromPosition: Int, offset: Int) {
        val formatsToUpdate = formatting.filterKeys { it >= fromPosition }.toList()
        formatting.entries.removeAll { it.key >= fromPosition }
        formatsToUpdate.forEach { (pos, format) ->
            formatting[pos + offset] = format
        }
    }
    
    fun getFormattedText(): String {
        val result = StringBuilder()
        var currentFormat: TextFormat? = null
        
        for (i in 0 until content.length) {
            val format = formatting[i]
            if (format != currentFormat) {
                if (currentFormat != null) {
                    result.append(currentFormat.closeTag)
                }
                if (format != null) {
                    result.append(format.openTag)
                }
                currentFormat = format
            }
            result.append(content[i])
        }
        
        if (currentFormat != null) {
            result.append(currentFormat.closeTag)
        }
        
        return result.toString()
    }
}

data class TextFormat(
    val isBold: Boolean = false,
    val isItalic: Boolean = false
) {
    val openTag: String
        get() = buildString {
            if (isBold) append("<b>")
            if (isItalic) append("<i>")
        }
    
    val closeTag: String
        get() = buildString {
            if (isItalic) append("</i>")
            if (isBold) append("</b>")
        }
}

data class Position(val index: Int) {
    fun isValid(maxLength: Int): Boolean = index in 0..maxLength
}


