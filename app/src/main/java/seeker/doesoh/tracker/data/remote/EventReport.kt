package seeker.doesoh.tracker.data.remote

data class EventReport(
    val attributes: Map<String,Any>,
    val deviceId: Long,
    val eventTime: String?,
    val serverTime: String?,
    val geofenceId: Long,
    val id: Long,
    val maintenanceId: Long,
    val positionId: Long,
    val type: String
)