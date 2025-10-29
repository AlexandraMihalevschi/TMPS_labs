package domain.factory

import domain.models.*

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