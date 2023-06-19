package seeker.doesoh.tracker.presentation.component


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.data.remote.Position
import seeker.doesoh.tracker.presentation.MainViewModel
import seeker.doesoh.tracker.util.Utilities


@SuppressLint("UnrememberedMutableState")
@Composable
fun CustomInfoWindow(
    formattedString: String,
    title: String,
    isCarInfoWindow: Boolean = true) {
//
//      val position = mainViewModel.latestPositions.observeAsState().value?.get(deviceId)
        Log.d("InsideCustom ", "$title => $formattedString")
//        var updatedFormattedString by remember {
//            mutableStateOf(
//                position?.let {
//                    Utilities.formatInfoWindowString(
//                        it,
//                        speedUnit,
//                        distanceUnit,
//                        isTwelveHourFormat
//                    )
//                }
//            )
//        }
//        LaunchedEffect(position) {
//            Log.d("InsideCustom ", "Launch Effect run")
//            updatedFormattedString = Utilities.formatInfoWindowString(
//                    position,
//                    speedUnit,
//                    distanceUnit,
//                    isTwelveHourFormat
//                )
//
//        }
            
            Card(modifier = Modifier
                .widthIn(min = 300.dp, max = 350.dp)
                .heightIn(min = 160.dp),
                backgroundColor = Color.White,
                shape = RoundedCornerShape(10.dp),
                elevation = 10.dp ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "$title", modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp), textAlign = TextAlign.Center, fontSize = 18.sp)
                    Text(text = formattedString,modifier = Modifier.padding(horizontal = 3.dp))
                    if(isCarInfoWindow) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Click to Show Report....", fontSize = 12.sp,modifier = Modifier.padding(vertical = 5.dp, horizontal = 3.dp))
                    }
//                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                    IconButton(onClick = { /*TODO*/ }, modifier = Modifier.height(30.dp)) {
//                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Route", tint = Color.Green)
//                    }
//                }
//                Text(text = updatedFixtime , fontSize = 12.sp)
//                Text(text = "Address: ${updatedPosition.address} ", fontSize = 12.sp,modifier = Modifier.padding(vertical = 3.dp))
//                Text(text = "Speed: ${updatedPosition.speed.toInt()} ", fontSize = 12.sp, modifier = Modifier.padding(vertical = 3.dp))
//                if(updatedPosition.attributes.containsKey("ignition")) {
//                    val ignition = if(updatedPosition.attributes["ignition"] as Boolean) "Yes" else "No"
//                    Text(text = "Ignition: $ignition", fontSize = 12.sp,modifier = Modifier.padding(vertical = 3.dp))
//                }
//                if(updatedPosition.attributes.containsKey("totalDistance")) {
//                    Text(text = "Total Distance: ${updatedPosition.attributes["totalDistance"]} ", fontSize = 12.sp, fontWeight = FontWeight.Light, modifier = Modifier.padding(vertical = 3.dp))
//                }

                }

            }
        }


@Preview(showBackground = true)
@Composable
fun PreviewInfoWindow() {
//    CustomInfoWindow("time: 123123", "GBI NEW")
}