package seeker.doesoh.tracker.data.remote

data class Event(
    val attributes: Map<String,Any>,
    val deviceId: Long,
    val serverTime: String,
    val geofenceId: Long,
    val id: Long,
    val maintenanceId: Long,
    val positionId: Long,
    val type: String
)