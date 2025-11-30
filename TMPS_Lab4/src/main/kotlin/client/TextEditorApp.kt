package client

import domain.commands.*
import domain.models.TextDocument
import domain.models.TextFormat
import domain.observers.*
import domain.states.EditorContext
import domain.states.NormalState
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Main text editor application
 * Demonstrates Command, Observer, and State patterns
 */
class TextEditorApp {
    private val document = TextDocument()
    private val commandHistory = CommandHistory()
    private val documentSubject = DocumentSubject()
    private val editorContext = EditorContext()
    private var cursorPosition = 0
    private val reader = BufferedReader(InputStreamReader(System.`in`))
    
    init {
        // Attach observers (Observer Pattern)
        documentSubject.attach(ConsoleObserver("UI"))
        documentSubject.attach(StatisticsObserver())
    }
    
    fun run() {
        printWelcomeBanner()
        
        var running = true
        var commandCount = 0
        
        while (running) {
            clearScreen()
            printHeader()
            printDocument()
            printDocumentInfo()
            printStatus()
            printCommandHistory()
            
            val prompt = editorContext.getPrompt()
            print(prompt)
            
            // Flush output to ensure prompt is displayed
            System.out.flush()
            
            // Use BufferedReader for more reliable input
            val input = try {
                reader.readLine()?.trim()
            } catch (e: Exception) {
                println("\nError reading input. Exiting...")
                break
            }
            
            // Handle EOF or null input (user closed stdin or Ctrl+Z)
            if (input == null) {
                println("\nExiting...")
                break
            }
            
            if (input.isEmpty()) continue
            
            when {
                input == "quit" || input == "q" -> {
                    running = false
                    continue
                }
                
                input == "help" || input == "h" -> {
                    printHelp()
                    println("\nPress Enter to continue...")
                    reader.readLine()
                    continue
                }
                
                input == "stats" -> {
                    printDetailedStats()
                    println("\nPress Enter to continue...")
                    reader.readLine()
                    continue
                }
                
                input.startsWith("insert ") -> {
                    commandCount++
                    handleInsert(input)
                    println("\n[Command #$commandCount executed] Press Enter to continue...")
                    reader.readLine()
                }
                
                input.startsWith("delete ") -> {
                    commandCount++
                    handleDelete(input)
                    println("\n[Command #$commandCount executed] Press Enter to continue...")
                    reader.readLine()
                }
                
                input.startsWith("format ") -> {
                    commandCount++
                    handleFormat(input)
                    println("\n[Command #$commandCount executed] Press Enter to continue...")
                    reader.readLine()
                }
                
                input == "undo" -> {
                    if (commandHistory.undo(document)) {
                        documentSubject.notifyDocumentChanged(document)
                        println("✓ Command undone successfully")
                    } else {
                        println("✗ Nothing to undo")
                    }
                    println("\nPress Enter to continue...")
                    reader.readLine()
                }
                
                input == "redo" -> {
                    if (commandHistory.redo(document)) {
                        documentSubject.notifyDocumentChanged(document)
                        println("✓ Command redone successfully")
                    } else {
                        println("✗ Nothing to redo")
                    }
                    println("\nPress Enter to continue...")
                    reader.readLine()
                }
                
                input.startsWith("cursor ") -> {
                    handleCursor(input)
                    println("\nPress Enter to continue...")
                    reader.readLine()
                }
                
                else -> {
                    // Let state handle the input (State Pattern)
                    val result = editorContext.handleInput(input)
                    println(result)
                    println("\nPress Enter to continue...")
                    reader.readLine()
                }
            }
        }
        
        printGoodbyeMessage()
    }
    
    /**
     * Helper extension function to center text within a given width
     */
    private fun String.padCenter(width: Int, padChar: Char = ' '): String {
        if (this.length >= width) return this
        val padding = width - this.length
        val leftPadding = padding / 2
        val rightPadding = padding - leftPadding
        return padChar.toString().repeat(leftPadding) + this + padChar.toString().repeat(rightPadding)
    }
    
    private fun printWelcomeBanner() {
        println("\n" + "═".repeat(70))
        println(" ".repeat(20) + "TMPS Lab 4 - Text Editor" + " ".repeat(20))
        println(" ".repeat(15) + "Behavioral Design Patterns Demo" + " ".repeat(15))
        println("═".repeat(70))
        println(" Patterns: Command | Observer | State" + " ".repeat(35))
        println("═".repeat(70))
        println()
    }
    
    private fun clearScreen() {
        // Clear screen for better UX (works on most terminals)
        print("\u001b[H\u001b[2J")
        System.out.flush()
    }
    
    private fun printHeader() {
        val timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
        )
        println("═".repeat(70))
        println("  TMPS Text Editor  │  Session Time: $timestamp" + " ".repeat(30))
        println("═".repeat(70))
        println()
    }
    
    private fun printDocument() {
        println("─".repeat(70))
        println(" DOCUMENT CONTENT ".padCenter(68, ' '))
        println("─".repeat(70))
        if (document.getLength() == 0) {
            println(" ".repeat(68))
            println("  (Document is empty - start by inserting text)" + " ".repeat(26))
            println(" ".repeat(68))
        } else {
            val text = document.getText()
            val lines = text.split("\n")
            if (lines.isEmpty()) {
                println(" ".repeat(68))
            } else {
                lines.forEachIndexed { index, line ->
                    val lineNum = (index + 1).toString().padStart(4, ' ')
                    val displayLine = if (line.length > 60) line.take(57) + "..." else line
                    println(" $lineNum │ $displayLine" + " ".repeat(68 - 10 - displayLine.length))
                }
            }
        }
        println("─".repeat(70))
        println()
    }
    
    private fun printDocumentInfo() {
        val wordCount = if (document.getLength() > 0) {
            document.getText().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        } else 0
        val formattedChars = document.formatting.size
        val formattedPercent = if (document.getLength() > 0) {
            (formattedChars * 100.0 / document.getLength()).toInt()
        } else 0
        
        println("─ Document Statistics " + "─".repeat(47))
        println("  Length: ${document.getLength()} chars  │  Words: $wordCount  │  Formatted: $formattedChars chars ($formattedPercent%)")
        println("─".repeat(70))
        println()
    }
    
    private fun printStatus() {
        val state = editorContext.getState()::class.simpleName?.replace("State", "") ?: "Unknown"
        val canUndo = if (commandHistory.canUndo()) "✓ Available" else "✗ None"
        val canRedo = if (commandHistory.canRedo()) "✓ Available" else "✗ None"
        
        println("─ Editor Status " + "─".repeat(52))
        println("  Mode: $state  │  Cursor Position: $cursorPosition  │  Undo: $canUndo  │  Redo: $canRedo")
        println("─".repeat(70))
        println()
    }
    
    private fun printCommandHistory() {
        println("─ Quick Reference " + "─".repeat(51))
        println("  Commands: insert | delete | format | undo | redo | cursor | help | stats | quit")
        println("  Modes: 'i' = Insert | 'v' = Visual | 'esc' = Normal")
        println("─".repeat(70))
        println()
    }
    
    private fun printHelp() {
        clearScreen()
        println("═".repeat(70))
        println(" HELP - TMPS Text Editor Commands ".padCenter(68, ' '))
        println("═".repeat(70))
        println()
        println("  TEXT OPERATIONS:")
        println("    insert <pos> <text>     - Insert text at position")
        println("    delete <pos> <len>      - Delete text from position")
        println("    format <pos> <len> <type> - Format text (bold|italic|both)")
        println()
        println("  HISTORY OPERATIONS:")
        println("    undo                    - Undo last command")
        println("    redo                    - Redo last undone command")
        println()
        println("  NAVIGATION:")
        println("    cursor <pos>            - Move cursor to position")
        println()
        println("  MODE SWITCHING (State Pattern):")
        println("    i                       - Enter INSERT mode")
        println("    v                       - Enter VISUAL mode")
        println("    esc                     - Return to NORMAL mode")
        println()
        println("  UTILITY:")
        println("    help, h                 - Show this help")
        println("    stats                   - Show detailed statistics")
        println("    quit, q                 - Exit editor")
        println()
        println("═".repeat(70))
    }
    
    private fun printDetailedStats() {
        clearScreen()
        val wordCount = if (document.getLength() > 0) {
            document.getText().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        } else 0
        val charCount = document.getLength()
        val formattedCount = document.formatting.size
        val lineCount = if (charCount > 0) document.getText().split("\n").size else 0
        
        println("═".repeat(70))
        println(" DOCUMENT STATISTICS ".padCenter(68, ' '))
        println("═".repeat(70))
        println()
        println("  Content Metrics:")
        println("    Total Characters: $charCount")
        println("    Total Words: $wordCount")
        println("    Total Lines: $lineCount")
        println("    Formatted Characters: $formattedCount")
        println()
        println("  Editor State:")
        println("    Current Mode: ${editorContext.getState()::class.simpleName}")
        println("    Cursor Position: $cursorPosition")
        println("    Can Undo: ${if (commandHistory.canUndo()) "Yes" else "No"}")
        println("    Can Redo: ${if (commandHistory.canRedo()) "Yes" else "No"}")
        println()
        println("  Observers Active: 2 (ConsoleObserver, StatisticsObserver)")
        println()
        println("═".repeat(70))
    }
    
    private fun printGoodbyeMessage() {
        clearScreen()
        println("\n" + "═".repeat(70))
        println()
        println(" Thank you for using TMPS Text Editor! ".padCenter(68, ' '))
        println()
        println(" This application demonstrates: ".padCenter(68, ' '))
        println("   • Command Pattern (Undo/Redo) ".padCenter(68, ' '))
        println("   • Observer Pattern (Change Notifications) ".padCenter(68, ' '))
        println("   • State Pattern (Editor Modes) ".padCenter(68, ' '))
        println()
        println("═".repeat(70))
        println()
    }
    
    private fun handleInsert(input: String) {
        val parts = input.substringAfter("insert ").split(" ", limit = 2)
        if (parts.size != 2) {
            println("Usage: insert <position> <text>")
            return
        }
        
        val position = parts[0].toIntOrNull()
        val text = parts[1]
        
        if (position == null) {
            println("Invalid position")
            return
        }
        
        val command = InsertCommand(position, text)
        if (commandHistory.executeCommand(command, document)) {
            documentSubject.notifyDocumentChanged(document)
            cursorPosition = position + text.length
            documentSubject.notifyCursorMoved(cursorPosition)
            println("✓ Successfully inserted '$text' at position $position")
            println("  → Cursor moved to position $cursorPosition")
        } else {
            println("✗ Failed to insert: Invalid position (must be 0-${document.getLength()})")
        }
    }
    
    private fun handleDelete(input: String) {
        val parts = input.substringAfter("delete ").split(" ")
        if (parts.size != 2) {
            println("Usage: delete <position> <length>")
            return
        }
        
        val position = parts[0].toIntOrNull()
        val length = parts[1].toIntOrNull()
        
        if (position == null || length == null) {
            println("Invalid position or length")
            return
        }
        
        val command = DeleteCommand(position, length)
        if (commandHistory.executeCommand(command, document)) {
            documentSubject.notifyDocumentChanged(document)
            cursorPosition = position.coerceAtMost(document.getLength())
            documentSubject.notifyCursorMoved(cursorPosition)
            val deletedText = if (length <= 20) {
                document.getText().substring(position.coerceAtMost(document.getLength()))
                    .take(length)
            } else {
                document.getText().substring(position.coerceAtMost(document.getLength()))
                    .take(17) + "..."
            }
            println("✓ Successfully deleted $length character(s) from position $position")
            println("  → Removed text: '$deletedText'")
            println("  → Cursor moved to position $cursorPosition")
        } else {
            println("✗ Failed to delete: Invalid range (position: $position, length: $length, doc length: ${document.getLength()})")
        }
    }
    
    private fun handleFormat(input: String) {
        val parts = input.substringAfter("format ").split(" ")
        if (parts.size != 3) {
            println("Usage: format <position> <length> <bold|italic|both>")
            return
        }
        
        val position = parts[0].toIntOrNull()
        val length = parts[1].toIntOrNull()
        val formatType = parts[2].lowercase()
        
        if (position == null || length == null) {
            println("Invalid position or length")
            return
        }
        
        val format = when (formatType) {
            "bold" -> TextFormat(isBold = true)
            "italic" -> TextFormat(isItalic = true)
            "both" -> TextFormat(isBold = true, isItalic = true)
            else -> {
                println("Invalid format type. Use: bold, italic, or both")
                return
            }
        }
        
        val command = FormatCommand(position, length, format)
        if (commandHistory.executeCommand(command, document)) {
            documentSubject.notifyDocumentChanged(document)
            val formatDesc = buildString {
                if (format.isBold) append("bold")
                if (format.isItalic) {
                    if (isNotEmpty()) append("+")
                    append("italic")
                }
            }
            println("✓ Successfully applied $formatDesc formatting to $length character(s) at position $position")
        } else {
            println("✗ Failed to format: Invalid range (position: $position, length: $length, doc length: ${document.getLength()})")
        }
    }
    
    private fun handleCursor(input: String) {
        val position = input.substringAfter("cursor ").toIntOrNull()
        if (position == null) {
            println("Usage: cursor <position>")
            return
        }
        
        if (position in 0..document.getLength()) {
            cursorPosition = position
            documentSubject.notifyCursorMoved(cursorPosition)
            println("✓ Cursor moved to position $position (document length: ${document.getLength()})")
        } else {
            println("✗ Invalid cursor position: $position (valid range: 0-${document.getLength()})")
        }
    }
}

fun main() {
    val app = TextEditorApp()
    app.run()
}

