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