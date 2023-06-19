package seeker.doesoh.tracker.presentation.Map



import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties

data class MapState(
    val properties: MapProperties = MapProperties(),
    val isFalloutMap: Boolean = false,
    val isReportMode: Boolean = false,
    val cameraPosition: LatLng = LatLng(16.8310404, 96.1280811)
)