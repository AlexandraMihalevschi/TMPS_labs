# Laboratory Work 2: Software Design – Creational Design Patterns

**Course**: Software Engineering and Design
**Author**: Mihalevschi Alexandra

---

### Theory

**Creational design patterns** are software design patterns that deal with object creation mechanisms. They abstract the instantiation process, making a system independent of how its objects are created, composed, and represented. These patterns provide flexibility in deciding which objects need to be created for a given case.

The five main creational patterns are:

1. **Singleton Pattern:**
   Ensures a class has only one instance throughout the application and provides a global point of access to it. This is useful for managing shared resources like configuration managers, logging systems, or registries.

2. **Factory Method Pattern:**
   Defines an interface for creating objects but lets subclasses decide which class to instantiate. The Factory Method delegates the instantiation logic to subclasses, promoting loose coupling between the creator and the concrete products.

3. **Abstract Factory Pattern:**
   Provides an interface for creating families of related or dependent objects without specifying their concrete classes. It's like a factory of factories, used when the system needs to work with multiple families of products.

4. **Builder Pattern:**
   Separates the construction of a complex object from its representation, allowing the same construction process to create different representations. It's particularly useful when an object requires many optional parameters or complex initialization steps.

5. **Prototype Pattern:**
   Creates new objects by copying existing instances (prototypes) rather than creating new ones from scratch. This is useful when object creation is expensive or when you need to avoid complex initialization logic.

For this laboratory work, **three creational patterns** were implemented in a Museum Registry System:

* **Singleton Pattern** – for the museum artifact registry
* **Factory Method Pattern** – for creating different types of museum artifacts
* **Builder Pattern** – for constructing complex exhibition objects

---

### Objectives

* **Understand** and **apply** creational design patterns in Kotlin.
* **Demonstrate** how these patterns improve code flexibility and maintainability.
* **Build** a console-based *Museum Registry System* showcasing pattern implementation.

---

### Implementation Description

**Project name:** `MuseumRegistrySystem`
**Language:** Kotlin

---

#### System Overview

The application simulates a **Museum Registry Management System**, where users can:

* Add different types of artifacts (paintings, sculptures, historical artifacts)
* View all registered artifacts
* Create exhibitions with selected artifacts
* Navigate through a validated menu interface

The system incorporates three creational design patterns along with comprehensive input handling classes to ensure data integrity and user-friendly interaction.

---

#### 1. Singleton Pattern – Museum Registry

The **Singleton Pattern** ensures that only one instance of the museum registry exists throughout the application, providing centralized artifact management.

```kotlin
object MuseumRegistry {
    private val artifacts = mutableListOf<MuseumArtifact>()

    fun addArtifact(artifact: MuseumArtifact) {
        artifacts.add(artifact)
        println("✓ Registered: ${artifact.name} (ID: ${artifact.id})")
    }

    fun listAllArtifacts() {
        println("\n=== Museum Registry ===")
        artifacts.forEach { println("  - ${it.name} [${it.category}] - ${it.origin}") }
        println("Total artifacts: ${artifacts.size}\n")
    }

    fun findById(id: String): MuseumArtifact? = artifacts.find { it.id == id }
}
```

**Key characteristics:**
* Uses Kotlin's `object` keyword for built-in singleton implementation
* Maintains a single, shared list of all museum artifacts
* Provides global access point for artifact registration and retrieval
* Thread-safe by default in Kotlin

**Benefits:**
* Ensures consistent artifact data across the entire application
* Prevents duplicate registries
* Simplifies artifact management with centralized control

---

#### 2. Factory Method Pattern – Artifact Creation

The **Factory Method Pattern** delegates the creation of different artifact types to specialized factory classes, allowing the system to create objects without specifying their exact classes.

```kotlin
abstract class MuseumArtifact(
    val id: String,
    val name: String,
    val category: String,
    val origin: String,
    val yearAcquired: Int
)

class Painting(
    id: String, name: String, origin: String, yearAcquired: Int,
    val artist: String,
    val medium: String
) : MuseumArtifact(id, name, "Painting", origin, yearAcquired)

class Sculpture(
    id: String, name: String, origin: String, yearAcquired: Int,
    val material: String,
    val height: Double
) : MuseumArtifact(id, name, "Sculpture", origin, yearAcquired)

class Artifact(
    id: String, name: String, origin: String, yearAcquired: Int,
    val period: String,
    val condition: String
) : MuseumArtifact(id, name, "Historical Artifact", origin, yearAcquired)

abstract class ArtifactFactory {
    abstract fun createArtifact(
        id: String,
        name: String,
        origin: String,
        yearAcquired: Int
    ): MuseumArtifact

    companion object {
        fun getFactory(type: String): ArtifactFactory {
            return when (type.lowercase()) {
                "painting" -> PaintingFactory()
                "sculpture" -> SculptureFactory()
                "artifact" -> HistoricalArtifactFactory()
                else -> throw IllegalArgumentException("Unknown artifact type: $type")
            }
        }
    }
}

class PaintingFactory : ArtifactFactory() {
    override fun createArtifact(id: String, name: String, origin: String, yearAcquired: Int): MuseumArtifact {
        return Painting(id, name, origin, yearAcquired, "Unknown Artist", "Oil on Canvas")
    }
}
```

**Key characteristics:**
* Abstract `ArtifactFactory` defines the creation interface
* Concrete factories (`PaintingFactory`, `SculptureFactory`, `HistoricalArtifactFactory`) implement specific creation logic
* Companion object provides factory selection based on artifact type
* Each artifact type has unique properties while sharing common base attributes

**Benefits:**
* Easy to add new artifact types without modifying existing code
* Encapsulates artifact creation logic
* Promotes loose coupling between client code and concrete artifact classes

---

#### 3. Builder Pattern – Exhibition Construction

The **Builder Pattern** constructs complex `Exhibition` objects step by step, allowing flexible configuration of optional parameters.

```kotlin
data class Exhibition(
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val curator: String,
    val artifacts: List<MuseumArtifact>,
    val isVirtual: Boolean,
    val maxVisitors: Int?
)

class ExhibitionBuilder {
    private var title: String = ""
    private var description: String = ""
    private var startDate: String = ""
    private var endDate: String = ""
    private var curator: String = "Unknown"
    private var artifacts: MutableList<MuseumArtifact> = mutableListOf()
    private var isVirtual: Boolean = false
    private var maxVisitors: Int? = null

    fun setTitle(title: String) = apply { this.title = title }
    fun setDescription(description: String) = apply { this.description = description }
    fun setStartDate(date: String) = apply { this.startDate = date }
    fun setEndDate(date: String) = apply { this.endDate = date }
    fun setCurator(curator: String) = apply { this.curator = curator }
    fun addArtifact(artifact: MuseumArtifact) = apply { this.artifacts.add(artifact) }
    fun setVirtual(virtual: Boolean) = apply { this.isVirtual = virtual }
    fun setMaxVisitors(max: Int) = apply { this.maxVisitors = max }

    fun build(): Exhibition {
        require(title.isNotEmpty()) { "Exhibition title is required" }
        require(startDate.isNotEmpty()) { "Start date is required" }
        require(artifacts.isNotEmpty()) { "At least one artifact is required" }

        return Exhibition(title, description, startDate, endDate, curator, artifacts, isVirtual, maxVisitors)
    }
}
```

**Key characteristics:**
* Fluent interface using `apply` for method chaining
* Validates required fields during `build()` phase
* Supports optional parameters with default values
* Separates construction logic from the exhibition representation

**Benefits:**
* Makes complex object creation more readable
* Allows step-by-step construction with validation
* Supports optional parameters without telescoping constructors
* Easy to add new exhibition properties without breaking existing code

---

#### Input Handling Architecture

To ensure robust user interaction, the system implements several supporting classes:

**InputValidator** – Validates different input types:
```kotlin
class InputValidator {
    fun validateId(id: String): InputResult<String>
    fun validateName(name: String): InputResult<String>
    fun validateYear(year: String): InputResult<Int>
    fun validateDate(date: String): InputResult<String>
    fun validateArtifactType(type: String): InputResult<String>
}
```

**InputReader** – Manages user input with retry logic:
```kotlin
class InputReader {
    fun readValidatedInput(prompt: String, validator: (String) -> InputResult<String>, maxRetries: Int = 3): String?
    fun readValidatedYear(prompt: String, maxRetries: Int = 3): Int?
    fun readConfirmation(prompt: String): Boolean
}
```

**Command Pattern** – Encapsulates menu actions:
```kotlin
abstract class MenuCommand {
    abstract fun execute()
}

class AddArtifactCommand(private val reader: InputReader) : MenuCommand()
class CreateExhibitionCommand(private val reader: InputReader) : MenuCommand()
class ListArtifactsCommand : MenuCommand()
```

---

#### Program Flow

1. **Main menu** displays available options
2. User selects an action (add artifact, create exhibition, etc.)
3. **InputReader** collects and validates user input with retry mechanism
4. **Factory Method** creates appropriate artifact type
5. **Singleton Registry** stores the artifact
6. **Builder Pattern** constructs exhibitions from selected artifacts
7. Application loops until user exits

---

#### Example Console Interaction

```
🏛️  Museum Registry System Demo

==== Menu ====
1. List all artifacts
2. Add a new artifact
3. Create an exhibition
4. Exit
Choose an option (1-4): 2

--- Add New Artifact ---
Enter artifact type (painting/sculpture/artifact): painting
Enter artifact ID: P001
Enter artifact name: Mona Lisa
Enter artifact origin: Italy
Enter year acquired: 1911
✓ Registered: Mona Lisa (ID: P001)
✅ Artifact added successfully!
```

Creating an exhibition:
```
--- Create New Exhibition ---
Enter exhibition title: Renaissance Masters
Enter description: A collection of Renaissance period artworks
Enter start date (YYYY-MM-DD): 2025-11-01
Enter end date (YYYY-MM-DD): 2026-01-31
Enter curator name: Dr. Smith

Select artifacts to add (enter IDs separated by comma):

=== Museum Registry ===
  - Mona Lisa [Painting] - Italy
  - David [Sculpture] - Italy
Total artifacts: 2

Artifact IDs: P001,S001

✅ Created exhibition: Renaissance Masters
   Curator: Dr. Smith
   Period: 2025-11-01 to 2026-01-31
   Artifacts: 2
```

---

### Conclusions

This laboratory work successfully demonstrates the practical application of **three fundamental creational design patterns** in Kotlin:

* **Singleton Pattern** provides centralized, consistent management of the artifact registry, ensuring a single source of truth for all museum data.

* **Factory Method Pattern** enables flexible artifact creation with proper encapsulation, making it easy to extend the system with new artifact types without modifying existing code.

* **Builder Pattern** simplifies the construction of complex exhibition objects with multiple optional parameters, improving code readability and maintainability.

The implementation also incorporates robust input validation and error handling through dedicated classes (`InputValidator`, `InputReader`), demonstrating how creational patterns work in conjunction with other design principles to create a **maintainable, extensible, and user-friendly application**.

These patterns exemplify how proper object creation strategies lead to cleaner architecture, reduced coupling, and improved testability – essential characteristics of professional software design. The Museum Registry System serves as a practical, educational example of how creational patterns solve real-world software engineering challenges.

---