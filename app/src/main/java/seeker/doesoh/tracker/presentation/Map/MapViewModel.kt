package seeker.doesoh.tracker.presentation.Map

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow

class MapViewModel: ViewModel() {

    var state by mutableStateOf(MapState())
    var cameraPositionLatLng = mutableStateOf(LatLng(16.8310404, 96.1280811))
//    var markerStateList = mutableStateMapOf<Long,MarkerState>()

    fun onEvent(event: MapEvent) {
        when(event) {
            is MapEvent.ToggleFalloutMap -> {
                state = state.copy(
                    properties = state.properties.copy(
                        mapStyleOptions =  if (state.isFalloutMap) {
                            null
                        }else MapStyleOptions(MapStyle.json)
                    ),
                    isFalloutMap = !state.isFalloutMap
                )
            }
            is MapEvent.MoveToDevicePosition -> {
                Log.d("mapviewmodel", "${event.position.longitude} : ${event.position.latitude}")
                cameraPositionLatLng.value = event.position
                Log.d("mapviewmodel", "${cameraPositionLatLng.value.latitude} : ${cameraPositionLatLng.value.longitude}")
            }
            is MapEvent.ReportMode -> {

            }
        }
    }
}