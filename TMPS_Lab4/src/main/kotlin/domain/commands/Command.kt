package domain.commands

import domain.models.TextDocument

/**
 * Command Pattern: Base interface for all document operations
 * Allows undo/redo functionality
 */
interface Command {
    fun execute(document: TextDocument): Boolean
    fun undo(document: TextDocument)
    fun redo(document: TextDocument)
}

/**
 * Command History Manager for undo/redo operations
 */
class CommandHistory {
    private val history = mutableListOf<Command>()
    private var currentIndex = -1
    
    fun executeCommand(command: Command, document: TextDocument): Boolean {
        // Remove any commands after current index (when undoing and then executing new command)
        if (currentIndex < history.size - 1) {
            history.subList(currentIndex + 1, history.size).clear()
        }
        
        if (command.execute(document)) {
            history.add(command)
            currentIndex++
            return true
        }
        return false
    }
    
    fun undo(document: TextDocument): Boolean {
        if (canUndo()) {
            history[currentIndex].undo(document)
            currentIndex--
            return true
        }
        return false
    }
    
    fun redo(document: TextDocument): Boolean {
        if (canRedo()) {
            currentIndex++
            history[currentIndex].redo(document)
            return true
        }
        return false
    }
    
    fun canUndo(): Boolean = currentIndex >= 0
    fun canRedo(): Boolean = currentIndex < history.size - 1
}


