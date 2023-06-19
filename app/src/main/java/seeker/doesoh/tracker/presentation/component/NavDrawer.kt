package seeker.doesoh.tracker.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.model.MenuItem
import seeker.doesoh.tracker.presentation.screen.getMenuItems


@Composable
fun NavHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(colorResource(id = R.color.colorPrimary)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start) {

        Box(modifier = Modifier.padding(20.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Doesoh Tracker",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun NavBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    onItemClick: (MenuItem) -> Unit,
    isNotificationEnabled: Boolean,
    onNotificationSet: (Boolean) -> Unit,
    onLogOutClicked: () -> Unit
) {
    LazyColumn() {
//        items(items) { item ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        onItemClick(item)
//                    }
//                    .padding(16.dp)
//            ) {
//                Icon(
//                    imageVector = item.icon,
//                    contentDescription = item.contentDescription,
//                    tint = colorResource(id = R.color.colorSecondary)
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                Text(
//                    text = item.title,
//                    style = TextStyle(fontSize = 18.sp),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }

        item {
            Column(
                modifier = Modifier
                    .fillParentMaxHeight(1f)
                    .fillParentMaxWidth(0.9f)
                    .padding(16.dp)) {
                //Notification
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(text = "Notification", fontSize = 18.sp)
                    Checkbox(
                        checked = isNotificationEnabled,
                        onCheckedChange =
                        {
                            onNotificationSet(it)
                        },
                        colors = CheckboxDefaults.colors(colorResource(id = R.color.colorSecondary)))

                }
                //Geofence
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Geofence", fontSize = 18.sp)
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                        colors = CheckboxDefaults.colors(colorResource(id = R.color.colorSecondary)))
                }
                Spacer(modifier = Modifier.weight(0.9f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            onLogOutClicked()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {

                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Log Out",
                        tint = colorResource(id = R.color.colorSecondary)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Log Out",
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.weight(1f)
                    )
                }

            }



        }

//        item {
//            Column(modifier = Modifier
//                .fillParentMaxHeight(0.7f)
//                .fillParentMaxWidth(0.8f)
//                .padding(16.dp),
//                verticalArrangement = Arrangement.Bottom) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clickable {
//                            onLogOutClicked()
//                        },
//                    verticalAlignment = Alignment.Bottom,
//                    horizontalArrangement = Arrangement.Start) {
//
//                    Icon(
//                        imageVector = Icons.Filled.Logout,
//                        contentDescription = "Log Out",
//                        tint = colorResource(id = R.color.colorSecondary)
//                    )
//                    Spacer(modifier = Modifier.width(16.dp))
//                    Text(
//                        text = "Log Out",
//                        style = TextStyle(fontSize = 18.sp),
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//        }
    }

}

@Preview
@Composable
fun NavPreview() {
    NavBody(items = getMenuItems(), onItemClick = {}, isNotificationEnabled = true, onNotificationSet = {}, onLogOutClicked = {})
}