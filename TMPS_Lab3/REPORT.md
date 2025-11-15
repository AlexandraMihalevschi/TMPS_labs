# Laboratory Work 3: Software Design – Structural Design Patterns

**Course**: Software Design
**Author**: Mihalevschi Alexandra

---

### Theory

**Structural design patterns** are software design patterns that deal with object composition, providing ways to organize classes and objects to form larger structures while keeping them flexible and efficient. These patterns focus on how classes and objects are composed to form larger structures, simplifying relationships between entities.

The main structural patterns include:

1. **Adapter Pattern:**
   Allows incompatible interfaces to work together by wrapping an object with an adapter that translates calls between interfaces. It acts as a bridge between two incompatible interfaces, enabling classes to work together that otherwise couldn't.

2. **Decorator Pattern:**
   Dynamically adds new responsibilities to objects without altering their structure. It provides a flexible alternative to subclassing for extending functionality, allowing behavior to be added to individual objects at runtime.

3. **Facade Pattern:**
   Provides a simplified, unified interface to a complex subsystem, hiding its complexity and making it easier to use. It defines a higher-level interface that makes the subsystem easier to work with.

4. **Bridge Pattern:**
   Separates an object's abstraction from its implementation, allowing them to vary independently.

5. **Composite Pattern:**
   Composes objects into tree structures to represent part-whole hierarchies, allowing clients to treat individual objects and compositions uniformly.

6. **Proxy Pattern:**
   Provides a surrogate or placeholder for another object to control access to it.

7. **Flyweight Pattern:**
   Minimizes memory usage by sharing as much data as possible with similar objects.

For this laboratory work, **three structural patterns** were implemented in a University Research Management System:

- **Adapter Pattern** – for converting research papers between different data formats
- **Decorator Pattern** – for dynamically enhancing research paper displays
- **Facade Pattern** – for simplifying complex database operations

---

### Objectives

- **Understand** and **apply** structural design patterns in Kotlin.
- **Demonstrate** how these patterns improve code flexibility, maintainability, and reusability.
- **Build** a graphical desktop application using **Compose for Desktop** showcasing pattern implementation.
- **Create** an interactive user interface that clearly demonstrates each pattern's functionality.

---

### Implementation Description

**Project name:** `University Research Management System`  
**Language:** Kotlin  
**UI Framework:** Compose for Desktop (Jetpack Compose Multiplatform)

---

#### System Overview

The application simulates a **University Research Management System**, where users can:

- Browse and search research papers from different departments
- Add new research papers with optional links/URLs
- Convert research papers between different formats (CSV, JSON, XML, BibTeX, Markdown)
- Dynamically enhance paper displays with citations, reviews, and formatting
- View system statistics and overview through a simplified interface

The system incorporates three structural design patterns along with a modern graphical user interface to provide an intuitive and educational demonstration of pattern implementation.

---

#### 1. Adapter Pattern – Format Conversion

The **Adapter Pattern** allows the system to convert research papers between different data formats without modifying the original paper objects. This pattern enables interoperability between incompatible interfaces.

**Implementation Structure:**

```kotlin
// Target interface for output adapters
interface PaperOutputAdapter {
    fun convert(papers: List<ResearchPaper>): String
    fun getFormatName(): String
    fun getFileExtension(): String
}

// Concrete adapters for different formats
class PaperToCSVAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        val header = "Paper ID,Title,Authors,Department,Year,Keywords,Abstract"
        val rows = papers.map { paper ->
            val authors = paper.authors.joinToString("; ")
            val keywords = paper.keywords.joinToString("; ")
            "\"${paper.id}\",\"${paper.title}\",\"$authors\",\"${paper.department}\",\"${paper.publicationDate}\",\"$keywords\",\"${paper.abstract}\""
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    override fun getFormatName(): String = "CSV"
    override fun getFileExtension(): String = "csv"
}

class PaperToJSONAdapter : PaperOutputAdapter {
    override fun convert(papers: List<ResearchPaper>): String {
        val jsonObjects = papers.map { paper ->
            """
            {
                "paperId": "${paper.id}",
                "title": "${paper.title}",
                "authors": [${paper.authors.joinToString(", ") { "\"$it\"" }}],
                "department": "${paper.department}",
                "year": "${paper.publicationDate}",
                "keywords": [${paper.keywords.joinToString(", ") { "\"$it\"" }}],
                "abstract": "${paper.abstract}"
            }
            """
        }
        return "[\n${jsonObjects.joinToString(",\n")}\n]"
    }

    override fun getFormatName(): String = "JSON"
    override fun getFileExtension(): String = "json"
}
```

**Key characteristics:**

- `PaperOutputAdapter` interface defines the target format for conversion
- Multiple concrete adapters (`PaperToCSVAdapter`, `PaperToJSONAdapter`, `PaperToXMLAdapter`, `PaperToBibTeXAdapter`, `PaperToMarkdownAdapter`) implement format-specific conversion logic
- Each adapter converts `ResearchPaper` objects to its respective format without modifying the original objects
- The pattern allows adding new output formats without changing existing code

**Benefits:**

- Enables format conversion without modifying source objects
- Easy to extend with new output formats
- Separates conversion logic from business logic
- Promotes single responsibility principle

**User Interaction:**
In the GUI, users can:

1. Select one or more research papers from the database
2. Choose an output format (CSV, JSON, XML, BibTeX, or Markdown)
3. Click "Convert" to see the papers transformed into the selected format
4. View the converted output in a scrollable, monospace-formatted display

---

#### 2. Decorator Pattern – Dynamic Feature Enhancement

The **Decorator Pattern** dynamically adds features to research papers (citations, peer reviews, formatting) without modifying their base structure. This allows runtime composition of features.

**Implementation Structure:**

```kotlin
// Component interface
interface PaperComponent {
    fun getDisplayInfo(): String
    fun getPaper(): ResearchPaper
}

// Concrete component
class BasePaperComponent(private val paper: ResearchPaper) : PaperComponent {
    override fun getDisplayInfo(): String {
        return paper.getFormattedInfo()
    }
    override fun getPaper(): ResearchPaper = paper
}

// Base decorator
abstract class PaperDecorator(protected val component: PaperComponent) : PaperComponent {
    override fun getPaper() = component.getPaper()
}

// Concrete decorators
class CitationDecorator(
    component: PaperComponent,
    private val database: ResearchDatabase
) : PaperDecorator(component) {
    override fun getDisplayInfo(): String {
        val baseInfo = component.getDisplayInfo()
        val citations = database.getCitations(getPaper().id)
        val citationText = if (citations.isNotEmpty()) {
            "\nCitations: ${citations.size} reference(s)"
        } else {
            "\nCitations: None"
        }
        return baseInfo + citationText
    }
}

class PeerReviewDecorator(
    component: PaperComponent,
    private val reviewStatus: String,
    private val reviewerComments: String
) : PaperDecorator(component) {
    override fun getDisplayInfo(): String {
        val baseInfo = component.getDisplayInfo()
        val reviewText = """

            Peer Review Status: $reviewStatus
            Reviewer Comments: $reviewerComments
        """.trimIndent()
        return baseInfo + reviewText
    }
}

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
```

**Key characteristics:**

- `PaperComponent` interface defines the common interface for components and decorators
- `BasePaperComponent` is the concrete component that provides basic paper information
- `PaperDecorator` is the abstract base class for all decorators
- Concrete decorators (`CitationDecorator`, `PeerReviewDecorator`, `FormattingDecorator`) add specific features
- Decorators can be composed in any order, allowing flexible feature combinations

**Benefits:**

- Adds functionality at runtime without modifying existing classes
- Allows flexible combination of features
- Follows open/closed principle (open for extension, closed for modification)
- Avoids class explosion that would occur with inheritance

**User Interaction:**
In the GUI, users can:

1. Select a research paper from the database
2. Choose which decorators to apply (Citations, Peer Review, Formatting)
3. Select formatting style (Academic or Modern)
4. Click "Apply Decorators" to see the enhanced paper display
5. View the decorated output in a scrollable preview pane

**Example Decorator Composition:**

```kotlin
var component: PaperComponent = BasePaperComponent(paper)

// Apply decorators in any order
if (includeFormatting) {
    component = FormattingDecorator(component, formatStyle)
}
if (includeCitations) {
    component = CitationDecorator(component, database)
}
if (includeReview) {
    component = PeerReviewDecorator(component, "In Review", review)
}

val decoratedInfo = component.getDisplayInfo()
```

---

#### 3. Facade Pattern – Simplified Database Interface

The **Facade Pattern** provides a simplified interface to the complex `ResearchDatabase` subsystem, hiding its complexity and making it easier to use.

**Implementation Structure:**

```kotlin
/**
 * Complex research database system with multiple subsystems
 */
class ResearchDatabase {
    private val papers = mutableListOf<ResearchPaper>()
    private val citations = mutableMapOf<String, List<String>>()
    private val reviews = mutableMapOf<String, String>()

    fun addPaper(paper: ResearchPaper) { /* ... */ }
    fun getPaper(id: String): ResearchPaper? { /* ... */ }
    fun getAllPapers(): List<ResearchPaper> { /* ... */ }
    fun searchByKeyword(keyword: String): List<ResearchPaper> { /* ... */ }
    fun searchByDepartment(department: String): List<ResearchPaper> { /* ... */ }
    fun addCitations(paperId: String, citationIds: List<String>) { /* ... */ }
    fun getCitations(paperId: String): List<String> { /* ... */ }
    fun addReview(paperId: String, review: String) { /* ... */ }
    fun getReview(paperId: String): String? { /* ... */ }
    fun getStatistics(): Map<String, Int> { /* ... */ }
}

/**
 * Facade Pattern: Provides a simplified interface to complex research database operations
 */
class ResearchFacade(private val database: ResearchDatabase) {

    /**
     * Simplified method to add a complete research paper with all related data
     */
    fun addCompleteResearchPaper(
        paper: ResearchPaper,
        citationIds: List<String> = emptyList(),
        reviewStatus: String = "Pending",
        reviewComments: String = ""
    ) {
        // Complex operation simplified through facade
        database.addPaper(paper)
        if (citationIds.isNotEmpty()) {
            database.addCitations(paper.id, citationIds)
        }
        if (reviewComments.isNotEmpty()) {
            database.addReview(paper.id, "$reviewStatus: $reviewComments")
        }
    }

    /**
     * Simplified search that searches across multiple criteria
     */
    fun searchResearch(query: String, searchType: String = "keyword"): List<ResearchPaper> {
        return when (searchType.lowercase()) {
            "keyword" -> database.searchByKeyword(query)
            "department" -> database.searchByDepartment(query)
            else -> database.getAllPapers().filter {
                it.title.contains(query, ignoreCase = true) ||
                it.authors.any { author -> author.contains(query, ignoreCase = true) }
            }
        }
    }

    /**
     * Get comprehensive paper information including citations and reviews
     */
    fun getPaperDetails(paperId: String): PaperDetails? {
        val paper = database.getPaper(paperId) ?: return null
        val citations = database.getCitations(paperId)
        val review = database.getReview(paperId)

        return PaperDetails(
            paper = paper,
            citationCount = citations.size,
            citations = citations,
            reviewStatus = review ?: "No review available"
        )
    }

    /**
     * Get system overview statistics
     */
    fun getSystemOverview(): SystemOverview {
        val stats = database.getStatistics()
        val allPapers = database.getAllPapers()
        val departments = allPapers.map { it.department }.distinct()

        return SystemOverview(
            totalPapers = stats["Total Papers"] ?: 0,
            papersWithCitations = stats["Papers with Citations"] ?: 0,
            papersWithReviews = stats["Papers with Reviews"] ?: 0,
            departments = departments.size,
            departmentList = departments
        )
    }
}
```

**Key characteristics:**

- `ResearchDatabase` is a complex subsystem with multiple responsibilities (papers, citations, reviews, statistics)
- `ResearchFacade` provides a simplified, unified interface
- Facade methods combine multiple database operations into single, easy-to-use methods
- Hides the complexity of interacting with multiple database subsystems
- Provides convenient methods like `addCompleteResearchPaper()` that handle multiple operations

**Benefits:**

- Simplifies client code by reducing the number of objects clients need to deal with
- Provides a single entry point to a complex subsystem
- Decouples client code from subsystem implementation
- Makes the subsystem easier to use and understand

**User Interaction:**
The facade is used throughout the application:

- **Browse Papers Tab**: Uses `searchResearch()` for unified searching
- **Add Paper Dialog**: Uses `addCompleteResearchPaper()` to add papers with all related data
- **System Overview Tab**: Uses `getSystemOverview()` to display statistics
- All operations are simplified through the facade interface

---

#### Project Structure

The project follows a clean architecture with clear separation of concerns:

```
src/main/kotlin/
├── Main.kt                          # Application entry point
├── client/
│   └── ResearchApp.kt              # GUI implementation using Compose for Desktop
├── domain/
│   ├── models/                     # Domain models
│   │   ├── ResearchPaper.kt        # Core paper entity
│   │   ├── ResearchData.kt         # Data transfer object
│   │   ├── ResearchDatabase.kt     # Complex database subsystem
│   │   ├── ResearchDataSource.kt   # Interface for data sources
│   │   └── PaperComponent.kt       # Component interface for Decorator
│   ├── builder/                    # Decorator pattern implementation
│   │   ├── BasePaperComponent.kt   # Concrete component
│   │   ├── PaperDecorator.kt       # Base decorator
│   │   ├── CitationDecorator.kt    # Adds citation information
│   │   ├── PeerReviewDecorator.kt  # Adds peer review status
│   │   └── FormattingDecorator.kt  # Adds formatting styles
│   └── factories/                  # Facade pattern implementation
│       └── ResearchFacade.kt       # Simplified database interface
└── utilities/                      # Adapter pattern implementation
    ├── ResearchDataSource.kt        # Input adapters (CSV, JSON, XML)
    ├── CSVDataSource.kt
    ├── JSONDataSource.kt
    ├── XMLDataSource.kt
    ├── ResearchDataAdapter.kt       # Input adapter (to ResearchPaper)
    ├── PaperOutputAdapter.kt        # Output adapter interface
    ├── PaperToCSVAdapter.kt         # Output adapters
    ├── PaperToJSONAdapter.kt
    ├── PaperToXMLAdapter.kt
    ├── PaperToBibTeXAdapter.kt
    └── PaperToMarkdownAdapter.kt
```

---

#### GUI Features

The application provides a modern, tabbed graphical interface built with **Compose for Desktop** and **Material Design 3**:

**1. Browse Papers Tab:**

- Search functionality with multiple search types (keyword, department, all)
- Interactive paper cards with expandable details
- Visual indicators for selected papers
- "Add Paper" button with dialog form
- Support for adding papers with optional URLs/links
- Result count display
- Scrollable paper list with visual scrollbars

**2. Adapter Demo Tab:**

- **Left Panel**: Paper selection with checkboxes
  - Select/deselect individual papers
  - "Select All" / "Deselect All" functionality
  - Visual feedback for selected papers
- **Right Panel**: Format conversion
  - Format selection (CSV, JSON, XML, BibTeX, Markdown)
  - Convert button with paper count
  - Scrollable output display with monospace font
  - Educational tooltip explaining the Adapter pattern

**3. Decorator Demo Tab:**

- **Left Panel**: Control panel
  - Paper selection dropdown
  - Decorator options (Citations, Peer Review, Formatting)
  - Format style selection (Academic, Modern)
  - Apply button
- **Right Panel**: Live preview
  - Real-time display of decorated paper
  - Scrollable formatted output
  - Empty state when no decorators applied

**4. System Overview Tab:**

- Statistics cards with icons and numbers
- Department list display
- Visual representation of system data
- All data accessed through the Facade pattern

**Key UI Features:**

- Material Design 3 components with modern styling
- Icons throughout for better visual communication
- Scrollable content areas with visible scrollbars
- Interactive elements with hover and click feedback
- Color-coded information (citations, reviews, statistics)
- Responsive layout that adapts to window size
- Empty states with helpful messages

---

### Conclusions

This laboratory work successfully demonstrates the practical application of **three fundamental structural design patterns** in Kotlin:

- **Adapter Pattern** enables seamless conversion between different data formats, allowing research papers to be exported to CSV, JSON, XML, BibTeX, and Markdown formats. The pattern provides flexibility in data representation without modifying the core `ResearchPaper` objects, making it easy to add new output formats in the future.

- **Decorator Pattern** allows dynamic enhancement of research paper displays with features like citations, peer reviews, and formatting styles. This pattern demonstrates runtime composition of features, avoiding the need for complex inheritance hierarchies and providing a flexible way to add functionality to objects.

- **Facade Pattern** simplifies interaction with the complex `ResearchDatabase` subsystem by providing a unified, easy-to-use interface. The facade hides the complexity of managing papers, citations, and reviews separately, making the system more maintainable and easier to use.

The implementation also incorporates a **modern graphical user interface** using Compose for Desktop, demonstrating how structural patterns work in conjunction with contemporary UI frameworks to create an **interactive, educational, and user-friendly application**.

These patterns exemplify how proper structural organization leads to:

- **Flexibility**: Easy to extend with new formats, decorators, or features
- **Maintainability**: Clear separation of concerns and responsibilities
- **Reusability**: Components can be combined in different ways
- **Simplicity**: Complex operations are hidden behind simple interfaces

The University Research Management System serves as a practical, educational example of how structural patterns solve real-world software engineering challenges, making complex systems more manageable and extensible while maintaining clean, readable code architecture.

---

### Technologies Used

- **Kotlin** 2.1.0 – Modern, concise programming language
- **Compose for Desktop** 1.8.0 – Declarative UI framework for desktop applications
- **Material Design 3** – Modern design system components
- **Gradle** – Build automation and dependency management

---

### Running the Application

1. Build the project: `./gradlew build` (or `.\gradlew build` on Windows)
3. Run the application: `./gradlew run` (or `.\gradlew run` on Windows)
