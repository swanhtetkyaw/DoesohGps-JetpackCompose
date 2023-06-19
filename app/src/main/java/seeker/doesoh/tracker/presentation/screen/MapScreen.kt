package seeker.doesoh.tracker.presentation.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import seeker.doesoh.tracker.data.model.MenuItem
import kotlinx.coroutines.launch
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.presentation.MainViewModel
import seeker.doesoh.tracker.presentation.Map.MapEvent
import seeker.doesoh.tracker.presentation.Map.MapViewModel
import seeker.doesoh.tracker.presentation.Screen
import seeker.doesoh.tracker.presentation.component.*
import seeker.doesoh.tracker.util.Utilities

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    mainViewModel: MainViewModel,
    navController: NavController
){
    val devices = mainViewModel.devices.observeAsState()
    val user = mainViewModel.user.value
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val chartBottomSheetState = mainViewModel.graphBottomSheetState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(16.8310404,96.1280811),10f,0f,0f)
    }
    val notificationEnabled = mainViewModel.notificationEnabled.collectAsState()
    val eventPoint = mainViewModel.eventPoint.collectAsState()
    val polylineList = mainViewModel.routeReportPolyLine.collectAsState().value
    val stopPoints = mainViewModel.routeStopPoints.collectAsState().value
    val highLightedTrip = mainViewModel.highLightedTrips.collectAsState().value
    Log.d("mapscreen: ", "Loaded")
    if(user != null) {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                NavHeader()
                NavBody(
                    items = getMenuItems(),
                    onItemClick = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                    isNotificationEnabled = notificationEnabled.value,
                    onNotificationSet = { notiState ->
                      mainViewModel.setNotification(notiState)
                    },
                    onLogOutClicked = {
                        mainViewModel.onMapUIEvent(MainViewModel.MapUIEvent.LogOut)
                    })
            },
            drawerBackgroundColor = colorResource(id = R.color.background_white),
            drawerShape = customShape(),
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen) {

            BoxWithConstraints(modifier = Modifier.fillMaxSize())
            {
                // Map
                Log.d("inside whole map body", " Called")
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
//                properties = mapViewModel.state.properties,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = true),
                    onMapLongClick = {},
                    onMapLoaded = {
                        mainViewModel.showSplashScreen.value = false
                        Log.d("Map", "Map is Loaded")
                    },
                    cameraPositionState = cameraPositionState,
                ) {
                    //Marker
                    devices.value?.let { devices ->
                        devices.forEach { (deviceId,device) ->
                            // I am Genius :D clap clap
                            Log.d("inside device", "$deviceId")
                            mainViewModel.markerStateList[deviceId]?.let { state ->
                                //TODO:: You Need to Reformat This
                                CarMarker(
                                    markerState = state,
                                    title = device.name,
                                    mainViewModel = mainViewModel,
                                    deviceId = deviceId,
                                    vehicleCategory = device.category ?: "truck",
                                    speedUnit = user.attributes.getOrDefault("speedUnit","kmh") as String,
                                    distanceUnit = user.attributes.getOrDefault("distanceUnit","km") as String,
                                    isTwelveHour = user.twelveHourFormat,
                                    visible = mainViewModel.markerVisibility.value,
                                    moveToPosition = { lat,lng ->
                                        scope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newCameraPosition(CameraPosition(
                                                    LatLng(lat,lng),
                                                    14f,
                                                    0f,
                                                    0f
                                                )),400)
                                        }

                                    },
                                    oninfoWindowClick = { deviceId ->
//                                        cameraPositionState.move(CameraUpdateFactory.newCameraPosition( CameraPosition(LatLng(lat, lng),17f,0f,0f)))
//                                        isReportMode = deviceId
                                            mainViewModel.reportMode.value = deviceId
                                    },
                                    onInfoWindowLongClick = {
                                        Log.d("info", "Long clicked")
                                    })

                                Log.d("insideCarMarker:", "marker called ${device.name}")
                                Log.d("insideCarMarker:", "marker called ${device.category}")
                                Log.d("insideCarMarker:", "marker called ${mainViewModel.latestDeviceStatus[deviceId]}")

                            }
                        }
                    }
                    //Polyline
                    if(mainViewModel.showingRoute.value) {
                        stopPoints.forEach { stopPoint ->
                            StopMarker(stopPoint,mainViewModel.stopMarkerVisiable)
                        }
                        if(polylineList.isNotEmpty()) {
                            val tripPositionList = mainViewModel.routeReportPolylineList
                            val startPosition = tripPositionList[0]
                            val endPosition = tripPositionList[tripPositionList.size - 1]
                            val midPointLat = polylineList[polylineList.size/2].latitude
                            val midPointLng = polylineList[polylineList.size/2].longitude
                            val startPointLat = startPosition.latitude
                            val startPointLng = startPosition.longitude
                            val endPointLat = endPosition.latitude
                            val endPointLng = endPosition.longitude

                            MarkerInfoWindowContent(
                                state = MarkerState(position = LatLng(startPointLat,startPointLng)),
                                icon = Utilities.getIcon(status = "online", category = "truck", position = startPosition),
                                rotation = startPosition.course.toFloat()
                            ) {
                                CustomInfoWindow(
                                    formattedString = Utilities.formatInfoWindowString(position = startPosition, speedUnit = "kmh", distanceUnit = "km", isTwelveHour = true),
                                    title = "Start Point",
                                    isCarInfoWindow = false)
                            }
                            MarkerInfoWindowContent(
                                state = MarkerState(position = LatLng(endPointLat,endPointLng)),
                                icon = Utilities.getIcon(status = "online", category = "truck", position = endPosition),
                                rotation = endPosition.course.toFloat()
                            ) {
                                CustomInfoWindow(
                                    formattedString = Utilities.formatInfoWindowString(position = endPosition, speedUnit = "kmh", distanceUnit = "km", isTwelveHour = true),
                                    title = "End Point",
                                    isCarInfoWindow = false)
                            }
                            MyPolyLine(points = polylineList)
                            LaunchedEffect(Unit) {
                                cameraPositionState.move(CameraUpdateFactory.newCameraPosition( CameraPosition(LatLng(midPointLat, midPointLng),12f,0f,0f)))
                            }
                        }
                        if(highLightedTrip.isNotEmpty()) {
                            val tripStartPoint = highLightedTrip[0]
                            Polyline(
                                points = highLightedTrip,
                                color = colorResource(id = R.color.colorPrimaryDark),
                                width = 18f,
                                zIndex = 100f
                            )
                            Log.d("tripCamera", "Called!!!")
                            cameraPositionState.move(CameraUpdateFactory.newCameraPosition((CameraPosition(tripStartPoint,12f,0f,0f))))
                        }

                    }
                    //Event Point Marker
                    if(eventPoint.value.isNotEmpty()) {
                        val position = eventPoint.value[0]
                        Log.d("eventPoint", "${position.fixTime} ")
                        val positionLatLng = LatLng(position.latitude, position.longitude)
                        EventMarker(position = position)
                        cameraPositionState.move(CameraUpdateFactory.newCameraPosition( CameraPosition(positionLatLng,12f,0f,0f)))

                    }
                }

                //TODO:: Detail Report view for one Device
                //Report Detail View and bottomSheet
                if(mainViewModel.reportMode.value != null) {
                    val deviceId = mainViewModel.reportMode.value as Long
//                    val isVisible = mainViewModel.ReportMode.value != null
                    val reportedDevice = devices.value?.get(deviceId)
                    reportedDevice?.let { device ->
                            ReportDetail(device,mainViewModel)
                        // Draw Graph Bottom Sheet
                            BottomSheetChart(
                                device= device,
                                sheetOpenState = chartBottomSheetState,
                                chartData = mainViewModel.chartData,
                                onClose = {
                                    mainViewModel.graphBottomSheetState.value = false
                                },
                                openChart = {
                                    mainViewModel.graphBottomSheetState.value = true
                                })
                    }


                }else {
                    //Normal Map View
                    // NavBar Button
                    MyFloatingActionButton(
                        onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                        modifier = Modifier.offset(10.dp,20.dp),
                        iconImage = Icons.Outlined.Menu)

                    // Right Side Two Button
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .offset((-10).dp, (-200).dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
                        MyFloatingActionButton(onClick = {
                            cameraPositionState.move(CameraUpdateFactory.newCameraPosition( CameraPosition(LatLng(16.8310404, 96.1280811),14f,0f,0f)))
                        }, iconImage = Icons.Outlined.Map, iconColor = colorResource(
                            id = R.color.colorPrimary
                        ))
                        Spacer(modifier = Modifier.height(10.dp))
                        MyFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                    //                         bottomSheetState.show()

                                } },
                            iconImage = Icons.Outlined.LocalShipping, iconColor = colorResource(
                                id = R.color.colorPrimary
                            ))
                    }



                    // Device BottomSheet
                    devices.value?.let { devices ->
                        BottomSheet(state = bottomSheetState, devices = devices) { deviceId ->
                            //Get Marker position and move cameraPosition
                            //TODO:: NOW U NEED TO SHOW FROM BOTTOM SHEET ABOUT DEVICE INFO
                            val markerState = mainViewModel.markerStateList[deviceId]
                            markerState?.let { markerState ->
                                markerState.showInfoWindow()
                                val location = markerState.position
//                            cameraPositionState.position = CameraPosition(location,18f,0f,0f)
                                cameraPositionState.move(CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(location,15f,0f,0f)
                                ))
                                scope.launch {
                                    bottomSheetState.hide()
                                }
                            }

                        }
                    }
                }

                //

                // close drawer if open when back button is pressed
                BackHandler(enabled = (scaffoldState.drawerState.isOpen || bottomSheetState.isVisible) || chartBottomSheetState.value) {
                    if(scaffoldState.drawerState.isOpen) {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    }else if(bottomSheetState.isVisible) {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }else if (mainViewModel.graphBottomSheetState.value) {
                        mainViewModel.graphBottomSheetState.value = false
                    }
                }

                //Show Splash Screen
//                if(mainViewModel.showSplashScreen.value) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(color = colorResource(id = R.color.colorPrimary)),
//                        verticalArrangement = Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally)
//                    {
//                        Image(
//                            painter = painterResource(id = R.drawable.ic_launcher_round),
//                            contentDescription = "Logo",
//                            contentScale = ContentScale.Fit,
//                            modifier = Modifier.size(100.dp)
//                        )
//                    }
//                }
            }
        }
    }

}

@Composable
fun MapFloatingActionButton(viewModel: MapViewModel) {
    FloatingActionButton(modifier = Modifier.offset(y = (-100).dp),onClick = {
        viewModel.onEvent(MapEvent.ToggleFalloutMap)
    }, elevation = FloatingActionButtonDefaults.elevation()) {
        Icon(
            imageVector = if (viewModel.state.isFalloutMap) {
                Icons.Default.ToggleOff
            } else Icons.Default.ToggleOn,
            contentDescription = "Toggle Fallout map"
        )
    }
}


@Composable
fun MyFloatingActionButton(onClick: () -> Unit,modifier: Modifier = Modifier,iconImage: ImageVector,iconColor: Color = Color.Gray,iconModifier: Modifier = Modifier) {
    FloatingActionButton(onClick = onClick,
        modifier = modifier
            .size(38.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(id = R.color.background_white),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)){
        Icon(imageVector = iconImage,contentDescription = "MY test", modifier = iconModifier
            .size(20.dp)
            .scale(scaleX = -1f, scaleY = 1f), tint = iconColor
        )
    }
}

fun getMenuItems() : List<MenuItem> {
    return listOf(
        MenuItem(id = "dashboard", title = "Dashboard", contentDescription = "My dashboard", icon = Icons.Outlined.Dashboard, route = "test"),
        MenuItem(id = "report", title = "Report", contentDescription = "My Report", icon = Icons.Outlined.Report, route = Screen.ReportScreen.route),
        MenuItem(id = "command", title = "Command", contentDescription = "My Command", icon = Icons.Outlined.SendToMobile, route = "test"),
        MenuItem(id = "setting", title = "Setting", contentDescription = "My Setting", icon = Icons.Outlined.Settings, route = "test"),
        MenuItem(id = "about", title = "About", contentDescription = "My About", icon = Icons.Outlined.QuestionAnswer, route = "test")
    )
}



//Custom width for Drawer
fun customShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rounded(RoundRect(0f,0f,800f,size.maxDimension, topRightCornerRadius = CornerRadius(5f,5f), bottomRightCornerRadius = CornerRadius(5f,5f)))
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreview() {
    Column(modifier = Modifier
        .fillMaxSize()
        .offset((-10).dp, (-50).dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End) {
        MyFloatingActionButton(onClick = { /*TODO*/ }, iconImage = Icons.Outlined.LocalShipping, iconColor = colorResource(
            id = R.color.colorPrimary
        ))
        Spacer(modifier = Modifier.height(10.dp))
        MyFloatingActionButton(onClick = { /*TODO*/ }, iconImage = Icons.Outlined.Savings, iconColor = colorResource(
            id = R.color.colorPrimary
        ))
    }
}


