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
