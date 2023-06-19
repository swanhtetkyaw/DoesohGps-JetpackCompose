package seeker.doesoh.tracker.data.remote

data class Command(
    val id: Long = 0,
    val deviceId: Long,
    val description: String = "New..",
    val type: String = "custom",
    val textChannel: Boolean = false,
    val attributes: Map<String,String>
)
