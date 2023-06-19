package seeker.doesoh.tracker.data.remote

data class User(
    val administrator: Boolean,
    var attributes: MutableMap<String,Any>,
    val coordinateFormat: String,
    val deviceLimit: Int,
    val deviceReadonly: Boolean,
    val disabled: Boolean,
    val email: String,
    val expirationTime: String,
    val id: Long,
    val latitude: Double,
    val limitCommands: Boolean,
    val longitude: Double,
    val map: String,
    val name: String,
    val password: String,
    val phone: String,
    val poiLayer: String,
    val readonly: Boolean,
    val twelveHourFormat: Boolean,
    val userLimit: Int,
    val zoom: Int
)