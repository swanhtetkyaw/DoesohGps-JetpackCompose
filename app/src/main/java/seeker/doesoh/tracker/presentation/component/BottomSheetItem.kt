package seeker.doesoh.tracker.presentation.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.remote.Device


@Composable
fun BottomSheetItem(device: Device, onDeviceClicked: (id: Long) -> Unit) {

    Log.d("BottomSheetItem", " ${device.name} => ${device.status}")
    val statusColor = when(device.status) {
        "offline" -> Color.Red
        "online" -> Color.Green
        else -> Color.Yellow
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(20.dp))
        .background(color = Color.Transparent)
        .height(60.dp)
        .clickable { onDeviceClicked(device.id) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Filled.LocalShipping,
            contentDescription = "Car",
            tint = colorResource(id = R.color.background_white))
        Spacer(modifier = Modifier.width(13.dp))
        Text(text = device.name ,
            Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.background_white))
        Box(modifier = Modifier
            .size(15.dp)
            .clip(shape = CircleShape)
            .background(color = statusColor, shape = CircleShape))
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewDeviceItem() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black), contentAlignment = Alignment.Center) {
    }

}