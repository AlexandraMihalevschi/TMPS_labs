package domain.states

/**
 * State Pattern: Base interface for editor states
 */
interface EditorState {
    fun handleInput(input: String, context: EditorContext): String
    fun getPrompt(): String
    fun getHelp(): String
}

/**
 * State Pattern: Context that maintains current state
 */
class EditorContext {
    private var state: EditorState = NormalState()
    
    fun setState(state: EditorState) {
        this.state = state
    }
    
    fun getState(): EditorState = state
    
    fun handleInput(input: String): String {
        return state.handleInput(input, this)
    }
    
    fun getPrompt(): String = state.getPrompt()
    fun getHelp(): String = state.getHelp()
}

/**
 * State Pattern: Normal mode - navigation and command mode
 */
class NormalState : EditorState {
    override fun handleInput(input: String, context: EditorContext): String {
        return when (input.lowercase()) {
            "i" -> {
                context.setState(InsertState())
                "Switched to INSERT mode. Type text, press ESC to return to normal mode."
            }
            "v" -> {
                context.setState(VisualState())
                "Switched to VISUAL mode. Select text, press ESC to return to normal mode."
            }
            "h" -> getHelp()
            else -> "Unknown command. Type 'h' for help."
        }
    }
    
    override fun getPrompt(): String = "NORMAL> "
    
    override fun getHelp(): String = """
        Normal Mode Commands:
        i - Enter INSERT mode
        v - Enter VISUAL mode
        h - Show this help
    """.trimIndent()
}

/**
 * State Pattern: Insert mode - text input mode
 */
class InsertState : EditorState {
    override fun handleInput(input: String, context: EditorContext): String {
        return when (input.lowercase()) {
            "esc" -> {
                context.setState(NormalState())
                "Returned to NORMAL mode."
            }
            else -> {
                "INSERT: $input"
            }
        }
    }
    
    override fun getPrompt(): String = "INSERT> "
    
    override fun getHelp(): String = """
        Insert Mode:
        Type text to insert
        ESC - Return to normal mode
    """.trimIndent()
}

/**
 * State Pattern: Visual mode - text selection mode
 */
class VisualState : EditorState {
    private var selectionStart: Int? = null
    
    override fun handleInput(input: String, context: EditorContext): String {
        return when (input.lowercase()) {
            "esc" -> {
                context.setState(NormalState())
                selectionStart = null
                "Returned to NORMAL mode."
            }
            "start" -> {
                selectionStart = 0
                "Selection started. Use 'end <pos>' to select text."
            }
            else -> {
                if (input.startsWith("end ")) {
                    val endPos = input.substringAfter("end ").toIntOrNull()
                    if (endPos != null && selectionStart != null) {
                        val result = "Selected text from $selectionStart to $endPos"
                        selectionStart = null
                        return result
                    }
                }
                "VISUAL: Use 'start' to begin selection, 'end <pos>' to finish, ESC to cancel"
            }
        }
    }
    
    override fun getPrompt(): String = "VISUAL> "
    
    override fun getHelp(): String = """
        Visual Mode:
        start - Begin text selection
        end <pos> - End selection at position
        ESC - Return to normal mode
    """.trimIndent()
}


