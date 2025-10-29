package client.commands

import client.input.InputReader
import client.validation.InputValidator
import domain.factory.ArtifactFactory
import domain.repository.MuseumRegistry
import domain.builder.ExhibitionBuilder

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