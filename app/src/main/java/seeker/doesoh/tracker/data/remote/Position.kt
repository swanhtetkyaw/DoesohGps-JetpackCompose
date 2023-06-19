package seeker.doesoh.tracker.data.remote

data class Position(
    val accuracy: Double,
    val address: String?,
    val altitude: Long,
    val attributes: Map<String,Any>,
    val course: Double,
    val deviceId: Long,
    val deviceTime: String,
    val fixTime: String,
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val network: Map<String,Any>,
    val outdated: Boolean,
    val protocol: String,
    val serverTime: String,
    val speed: Double,
    val valid: Boolean,
)