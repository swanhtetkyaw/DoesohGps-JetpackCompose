package seeker.doesoh.tracker.presentation.component


import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberMarkerState
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.remote.StopReport
import seeker.doesoh.tracker.util.Utilities

@Composable
fun StopMarker(stopReport: StopReport,visible: State<Boolean>) {
    //TODO:: Show stop point snippet
    val position = LatLng(stopReport.latitude,stopReport.longitude)
    val stopFormattedString = Utilities.formatStopInfoWindowString(stopReport)
    val markerState = rememberMarkerState(position = position)
    val icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_flag)
    MarkerInfoWindowContent(
        state = markerState,
        icon = icon,
        title = "Stop Point",
        visible = visible.value
    ) {
       CustomInfoWindow(formattedString = stopFormattedString, title = "Stop Point", isCarInfoWindow = false)
    }
}