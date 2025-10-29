package domain.models

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