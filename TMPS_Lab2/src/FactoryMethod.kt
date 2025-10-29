abstract class MuseumArtifact(
    val id: String,
    val name: String,
    val category: String,
    val origin: String,
    val yearAcquired: Int
)

class Painting(
    id: String,
    name: String,
    origin: String,
    yearAcquired: Int,
    val artist: String,
    val medium: String
) : MuseumArtifact(id, name, "Painting", origin, yearAcquired)

class Sculpture(
    id: String,
    name: String,
    origin: String,
    yearAcquired: Int,
    val material: String,
    val height: Double
) : MuseumArtifact(id, name, "Sculpture", origin, yearAcquired)

class Artifact(
    id: String,
    name: String,
    origin: String,
    yearAcquired: Int,
    val period: String,
    val condition: String
) : MuseumArtifact(id, name, "Historical Artifact", origin, yearAcquired)

// Factory for creating different types of artifacts
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

class SculptureFactory : ArtifactFactory() {
    override fun createArtifact(id: String, name: String, origin: String, yearAcquired: Int): MuseumArtifact {
        return Sculpture(id, name, origin, yearAcquired, "Bronze", 1.5)
    }
}

class HistoricalArtifactFactory : ArtifactFactory() {
    override fun createArtifact(id: String, name: String, origin: String, yearAcquired: Int): MuseumArtifact {
        return Artifact(id, name, origin, yearAcquired, "Ancient", "Good")
    }
}