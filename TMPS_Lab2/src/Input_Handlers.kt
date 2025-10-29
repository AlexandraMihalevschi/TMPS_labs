// Result wrapper for input validation
sealed class InputResult<out T> {
    data class Success<T>(val value: T) : InputResult<T>()
    data class Error(val message: String) : InputResult<Nothing>()
}

// Input validator for different data types
class InputValidator {
    fun validateId(id: String): InputResult<String> {
        return when {
            id.isBlank() -> InputResult.Error("ID cannot be empty")
            id.length < 3 -> InputResult.Error("ID must be at least 3 characters")
            !id.matches(Regex("[A-Za-z0-9_-]+")) -> InputResult.Error("ID can only contain letters, numbers, hyphens, and underscores")
            else -> InputResult.Success(id)
        }
    }

    fun validateName(name: String): InputResult<String> {
        return when {
            name.isBlank() -> InputResult.Error("Name cannot be empty")
            name.length < 2 -> InputResult.Error("Name must be at least 2 characters")
            else -> InputResult.Success(name)
        }
    }

    fun validateYear(year: String): InputResult<Int> {
        val yearInt = year.toIntOrNull()
        return when {
            yearInt == null -> InputResult.Error("Year must be a valid number")
            yearInt < 1000 -> InputResult.Error("Year must be 1000 or later")
            yearInt > 2025 -> InputResult.Error("Year cannot be in the future")
            else -> InputResult.Success(yearInt)
        }
    }

    fun validateDate(date: String): InputResult<String> {
        return when {
            date.isBlank() -> InputResult.Error("Date cannot be empty")
            !date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> InputResult.Error("Date must be in YYYY-MM-DD format")
            else -> InputResult.Success(date)
        }
    }

    fun validateArtifactType(type: String): InputResult<String> {
        val validTypes = listOf("painting", "sculpture", "artifact")
        return when {
            type.isBlank() -> InputResult.Error("Type cannot be empty")
            type.lowercase() !in validTypes -> InputResult.Error("Type must be one of: ${validTypes.joinToString(", ")}")
            else -> InputResult.Success(type.lowercase())
        }
    }
}

// Input reader with retry capability
class InputReader {
    private val validator = InputValidator()

    fun readString(prompt: String, default: String = ""): String {
        print(prompt)
        return readLine()?.trim()?.takeIf { it.isNotEmpty() } ?: default
    }

    fun readValidatedInput(
        prompt: String,
        validator: (String) -> InputResult<String>,
        maxRetries: Int = 3
    ): String? {
        repeat(maxRetries) { attempt ->
            print(prompt)
            val input = readLine()?.trim() ?: ""

            when (val result = validator(input)) {
                is InputResult.Success -> return result.value
                is InputResult.Error -> {
                    println("❌ ${result.message}")
                    if (attempt < maxRetries - 1) {
                        println("   Please try again (${maxRetries - attempt - 1} attempts remaining)")
                    }
                }
            }
        }
        println("❌ Max retries exceeded")
        return null
    }

    fun readValidatedYear(prompt: String, maxRetries: Int = 3): Int? {
        repeat(maxRetries) { attempt ->
            print(prompt)
            val input = readLine()?.trim() ?: ""

            when (val result = validator.validateYear(input)) {
                is InputResult.Success -> return result.value
                is InputResult.Error -> {
                    println("❌ ${result.message}")
                    if (attempt < maxRetries - 1) {
                        println("   Please try again (${maxRetries - attempt - 1} attempts remaining)")
                    }
                }
            }
        }
        println("❌ Max retries exceeded")
        return null
    }

    fun readConfirmation(prompt: String): Boolean {
        print("$prompt (y/n): ")
        return readLine()?.trim()?.lowercase() in listOf("y", "yes")
    }
}

// Command handler for menu actions
abstract class MenuCommand {
    abstract fun execute()
}

class ListArtifactsCommand : MenuCommand() {
    override fun execute() {
        MuseumRegistry.listAllArtifacts()
    }
}

class AddArtifactCommand(private val reader: InputReader) : MenuCommand() {
    private val validator = InputValidator()

    override fun execute() {
        println("\n--- Add New Artifact ---")

        val type = reader.readValidatedInput(
            "Enter artifact type (painting/sculpture/artifact): ",
            validator::validateArtifactType
        ) ?: return

        val id = reader.readValidatedInput(
            "Enter artifact ID: ",
            validator::validateId
        ) ?: return

        val name = reader.readValidatedInput(
            "Enter artifact name: ",
            validator::validateName
        ) ?: return

        val origin = reader.readString("Enter artifact origin: ", "Unknown")

        val yearAcquired = reader.readValidatedYear("Enter year acquired: ") ?: return

        try {
            val factory = ArtifactFactory.getFactory(type)
            val newArtifact = factory.createArtifact(id, name, origin, yearAcquired)
            MuseumRegistry.addArtifact(newArtifact)
            println("✅ Artifact added successfully!")
        } catch (e: Exception) {
            println("❌ Error: ${e.message}")
        }
    }
}

class CreateExhibitionCommand(private val reader: InputReader) : MenuCommand() {
    private val validator = InputValidator()

    override fun execute() {
        println("\n--- Create New Exhibition ---")

        val title = reader.readValidatedInput(
            "Enter exhibition title: ",
            validator::validateName
        ) ?: return

        val desc = reader.readString("Enter description: ", "No description provided")

        val startDate = reader.readValidatedInput(
            "Enter start date (YYYY-MM-DD): ",
            validator::validateDate
        ) ?: return

        val endDate = reader.readValidatedInput(
            "Enter end date (YYYY-MM-DD): ",
            validator::validateDate
        ) ?: return

        val curator = reader.readString("Enter curator name: ", "Unknown")

        val builder = ExhibitionBuilder()
            .setTitle(title)
            .setDescription(desc)
            .setStartDate(startDate)
            .setEndDate(endDate)
            .setCurator(curator)

        println("\nSelect artifacts to add (enter IDs separated by comma):")
        MuseumRegistry.listAllArtifacts()

        val artifactInput = reader.readString("Artifact IDs: ")
        val artifactIds = artifactInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        var addedCount = 0
        artifactIds.forEach { id ->
            val artifact = MuseumRegistry.findById(id)
            if (artifact != null) {
                builder.addArtifact(artifact)
                addedCount++
            } else {
                println("⚠️  Artifact with ID '$id' not found")
            }
        }

        if (addedCount == 0) {
            println("❌ No valid artifacts added. Exhibition creation cancelled.")
            return
        }

        try {
            val exhibition = builder.build()
            println("\n✅ Created exhibition: ${exhibition.title}")
            println("   Curator: ${exhibition.curator}")
            println("   Period: ${exhibition.startDate} to ${exhibition.endDate}")
            println("   Artifacts: ${exhibition.artifacts.size}")
        } catch (e: Exception) {
            println("❌ Error creating exhibition: ${e.message}")
        }
    }
}
