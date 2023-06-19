package seeker.doesoh.tracker.data.remote

data class Device(
    val attributes: Map<String,Any>,
    val category: String,
    val contact: String,
    val disabled: Boolean,
    val geofenceIds: List<Any>,
    val groupId: Long,
    val id: Long,
    val lastUpdate: String,
    val model: String,
    val name: String,
    val phone: String,
    val positionId: Long,
    val status: String,
    val uniqueId: String
)