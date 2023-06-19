package seeker.doesoh.tracker.presentation.component

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import seeker.doesoh.tracker.data.remote.Position
import seeker.doesoh.tracker.util.Utilities

@Composable
fun EventMarker(position: Position) {
    //TODO:: Show stop point snippet
    val positionLatLng = LatLng(position.latitude,position.longitude)
    val eventFormattedString = Utilities.formatInfoWindowString(position,"kmh","km",true)
    val markerState = MarkerState(position = positionLatLng)
    val icon = Utilities.getIcon(status = "online", category = "truck",position = position)
    MarkerInfoWindowContent(
        state = markerState,
        icon = icon,
        title = "Stop Point",
    ) {
        CustomInfoWindow(formattedString = eventFormattedString, title = "Stop Point", isCarInfoWindow = false)
    }
}