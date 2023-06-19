package seeker.doesoh.tracker.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.Polyline
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.remote.Position

@Composable
fun MyPolyLine(points: List<LatLng>) {
    Polyline(
        points = points,
        color = colorResource(id = R.color.colorSecondary),
    )
}