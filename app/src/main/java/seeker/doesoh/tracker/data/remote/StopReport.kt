package seeker.doesoh.tracker.data.remote

data class StopReport(
    val address: String,
    val deviceId: Long,
    val deviceName: String,
    val duration: Long,
    val endTime: String,
    val engineHours: Double,
    val latitude: Double,
    val longitude: Double,
    val spentFuel: Double,
    val startTime: String
)