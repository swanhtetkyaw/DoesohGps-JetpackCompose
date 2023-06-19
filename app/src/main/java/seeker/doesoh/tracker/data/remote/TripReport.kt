package seeker.doesoh.tracker.data.remote

data class TripReport(
    val averageSpeed: Double,
    val deviceId: Long,
    val deviceName: String,
    val distance: Double,
    val driverName: String,
    val driverUniqueId: Long,
    val duration: Double,
    val endAddress: String,
    val endLat: Double,
    val endLon: Double,
    val endTime: String,
    val maxSpeed: Double,
    val spentFuel: Double,
    val startAddress: String,
    val startLat: Double,
    val startLon: Double,
    val startTime: String
)