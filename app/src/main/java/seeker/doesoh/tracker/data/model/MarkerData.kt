package seeker.doesoh.tracker.data.model

import com.google.android.gms.maps.model.LatLng

data class MarkerData(
    val deviceId: Long,
    val location: LatLng
)