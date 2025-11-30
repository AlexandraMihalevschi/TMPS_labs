package domain.observers

import domain.models.TextDocument

/**
 * Observer Pattern: Interface for observers that watch document changes
 */
interface DocumentObserver {
    fun onDocumentChanged(document: TextDocument)
    fun onCursorMoved(position: Int)
}

/**
 * Observer Pattern: Subject that manages observers and notifies them of changes
 */
class DocumentSubject {
    private val observers = mutableListOf<DocumentObserver>()
    
    fun attach(observer: DocumentObserver) {
        observers.add(observer)
    }
    
    fun detach(observer: DocumentObserver) {
        observers.remove(observer)
    }
    
    fun notifyDocumentChanged(document: TextDocument) {
        observers.forEach { it.onDocumentChanged(document) }
    }
    
    fun notifyCursorMoved(position: Int) {
        observers.forEach { it.onCursorMoved(position) }
    }
}

/**
 * Observer Pattern: Console observer that displays document changes
 */
class ConsoleObserver(private val name: String) : DocumentObserver {
    override fun onDocumentChanged(document: TextDocument) {
        println("[$name] Document changed: ${document.getLength()} characters")
    }
    
    override fun onCursorMoved(position: Int) {
        println("[$name] Cursor moved to position: $position")
    }
}

/**
 * Observer Pattern: Statistics observer that tracks document metrics
 */
class StatisticsObserver : DocumentObserver {
    private var changeCount = 0
    private var lastCursorPosition = 0
    
    override fun onDocumentChanged(document: TextDocument) {
        changeCount++
        println("📊 Statistics: Document has been modified $changeCount times")
        println("   Current length: ${document.getLength()} characters")
        println("   Formatted sections: ${document.formatting.size}")
    }
    
    override fun onCursorMoved(position: Int) {
        lastCursorPosition = position
    }
    
    fun getChangeCount(): Int = changeCount
    fun getLastCursorPosition(): Int = lastCursorPosition
}


