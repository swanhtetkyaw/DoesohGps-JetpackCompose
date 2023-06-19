package seeker.doesoh.tracker.presentation.Map

import com.google.android.gms.maps.model.LatLng

sealed class MapEvent{
    object ToggleFalloutMap: MapEvent()
    data class ReportMode(val deviceId: Long): MapEvent()
    data class MoveToDevicePosition(val position: LatLng): MapEvent()
}