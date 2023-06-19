package seeker.doesoh.tracker.presentation.component

import android.annotation.SuppressLint
import android.text.BoringLayout
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.*
import com.google.maps.android.compose.*
import seeker.doesoh.tracker.presentation.MainViewModel
import seeker.doesoh.tracker.util.Utilities

@SuppressLint("UnrememberedMutableState")
@Composable
fun CarMarker(
    markerState: MarkerState,
    mainViewModel: MainViewModel,
    deviceId: Long,
    vehicleCategory: String,
    title: String,
    speedUnit: String,
    distanceUnit: String,
    isTwelveHour: Boolean,
    visible: Boolean,
    moveToPosition: (lat: Double,lng: Double) -> Unit,
    oninfoWindowClick: (deviceId: Long) -> Unit,
    onInfoWindowLongClick: () -> Unit) {
    //TODO:: Create Extension function for multi 'let' check
    val position  =  mainViewModel.latestDevicePositionList[deviceId]
//    val position = mainViewModel.latestPositions.observeAsState().value?.get(deviceId)
    val status = mainViewModel.latestDeviceStatus[deviceId] ?: "Unknown"
    position?.let { position ->
            val icon = Utilities.getIcon(status, vehicleCategory, position)
            val snippet= Utilities.formatInfoWindowString(position,speedUnit,distanceUnit,isTwelveHour)
            var snippetState by remember {
                mutableStateOf(snippet)
            }
            MarkerInfoWindowContent(
                state = markerState,
                title = title,
                rotation = position.course.toFloat(),
                icon = icon,
                snippet = snippet,
                onInfoWindowClick = {
                    it.hideInfoWindow()
                    oninfoWindowClick(deviceId)
                },
                //TODO:: After onclick is set it doesn't show google map redirect button
                onClick = {
                    // Just so we can see update during stop
                    // If device position is same makerState will not change
                    // TODO:: Later Update from State only, don't rely on webSocket Or Why not xD
                    if(!it.isInfoWindowShown) {
                        val p = mainViewModel.latestPositions.value?.get(deviceId)
                        p?.let { p ->
                            it.snippet = Utilities.formatInfoWindowString(p,speedUnit,distanceUnit,isTwelveHour)
                            moveToPosition(p.latitude,p.longitude)
                            it.showInfoWindow()
                        }
//                        it.snippet = p?.let { p -> Utilities.formatInfoWindowString(p,speedUnit,distanceUnit,isTwelveHour) }
//                        Log.d("InsideContentState: ", title + p?.fixTime)
//                        it.showInfoWindow()

                    }
                    true
                },
                visible = visible
            ) {
                Log.d("InsideContent: ", title + snippet)
                //TODO:: I think we can use snippet state in here directly?
//                LaunchedEffect(Unit) {
                    snippetState = it.snippet.toString()
//                }
                        CustomInfoWindow(
                            formattedString = snippetState,
                            title = title)
            }
    }




    Log.d("Marker: ", "$title is called")
    Log.d("CarMarker: ", " $title => ${position?.fixTime} : ${position?.speed}")
    Log.d("CarMarker: ", "${markerState.position}")

}