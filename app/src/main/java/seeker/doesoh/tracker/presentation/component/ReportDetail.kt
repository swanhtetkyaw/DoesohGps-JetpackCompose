package seeker.doesoh.tracker.presentation.component

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.model.CommandBtnState
import seeker.doesoh.tracker.data.remote.Device
import seeker.doesoh.tracker.presentation.MainViewModel
import seeker.doesoh.tracker.presentation.ReportDetail.CommandType
import seeker.doesoh.tracker.presentation.ReportDetail.ReportDate
import seeker.doesoh.tracker.presentation.ReportDetail.ReportDetailEvent
import seeker.doesoh.tracker.presentation.ReportDetail.ReportType
import seeker.doesoh.tracker.presentation.screen.MyFloatingActionButton
import seeker.doesoh.tracker.util.Utilities


@Composable
fun ReportDetail(device: Device,mainViewModel: MainViewModel) {

    //Ui state
    val state = mainViewModel.reportDetailUIState.value
    //Custom Date

    var isRouteDetailVisible by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.fillMaxSize())
    {

            // reopen route detail or close
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.End)
            {
                //Close RouteDetail
                AnimatedVisibility(
                    visible = !isRouteDetailVisible)
                {
                    MyFloatingActionButton(
                        onClick = {
                            mainViewModel.onReportDetailEvent(ReportDetailEvent.Close)
                        },
                        iconImage = Icons.Filled.Close,
                        iconColor = colorResource(id = R.color.colorPrimary),
                        modifier = Modifier.offset(y = 30.dp)
                    )
                }

                // Open RouteDetail
                AnimatedVisibility(visible = !isRouteDetailVisible) {
                    MyFloatingActionButton(
                        onClick = {
                            isRouteDetailVisible = true
                        },
                        iconImage = Icons.Filled.Route,
                        iconColor = colorResource(id = R.color.colorPrimary),
                        modifier = Modifier.offset(y = (-150).dp)
                    )
                }
            }
        //Report Detail Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 0.dp, 10.dp, 10.dp),
            verticalArrangement = Arrangement.Bottom)
        {
            // toggle stop marker
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
                AnimatedVisibility(visible = state.showRoutes) {
                    MyFloatingActionButton(
                        onClick = {
                            mainViewModel.stopMarkerVisiable.value = !mainViewModel.stopMarkerVisiable.value
                        },
                        //TODO:: Toggle between wrongLocation and addLocationAlt
                        iconImage = if(mainViewModel.stopMarkerVisiable.value) Icons.Filled.WrongLocation else Icons.Filled.AddLocationAlt,
                        iconColor = colorResource(id = R.color.colorPrimary),
                        modifier = Modifier
                            .width(80.dp)
                            .size(40.dp),
                        iconModifier = Modifier.size(25.dp)
                    )
                }
            }
            AnimatedVisibility(visible = isRouteDetailVisible) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(5.dp),
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = colorResource(id = R.color.background_white))
                {
                    Box(modifier = Modifier.fillMaxSize())
                    {

                        
                        if (state.isLoading) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (state.showReports) {
                            showReportOptions(mainViewModel = mainViewModel,device = device)
                        } else if (state.showRoutes) {
                            showRouteDetails(mainViewModel = mainViewModel, device = device)
                        } else if (state.showEvents) {
                            showEventDetails(mainViewModel = mainViewModel, device = device) {
                                isRouteDetailVisible = it
                            }
                        }
                        // TODO:: Long device name will collide with icon ,Drop down device name
                        // Back Button
                        if(state.showRoutes || state.showEvents) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp), horizontalArrangement = Arrangement.Start
                            )
                            {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back Arrow",
                                    modifier = Modifier.clickable {
                                        //Back to Report Detail
                                        mainViewModel.onReportDetailEvent(ReportDetailEvent.Back)
                                    })
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            //Minimize Icon
                            if(state.showRoutes) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Minimize",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable {
                                            //Minimize the ReportRoute
                                            isRouteDetailVisible = false
                                        })
                            }
                            //Close Icon
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                modifier = Modifier.clickable {
                                    //Close Report Detail
                                    mainViewModel.onReportDetailEvent(ReportDetailEvent.Close)
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportTypeItem(text: String,selectedType: String,icon: ImageVector,onSelectedType: (text: String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween)
    {
        IconButton(
            modifier = Modifier
                .size(50.dp)
                .padding(bottom = 8.dp)
                .background(
                    if (text == selectedType) colorResource(id = R.color.colorSecondary) else Color.LightGray,
                    CircleShape
                ),
            onClick = {
                onSelectedType(text)
            })
        {

            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
                    .border(1.dp, Color.Transparent, shape = CircleShape)
            )
        }

        Text(text = text,color = Color.Black)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showRouteDetails(mainViewModel: MainViewModel,device: Device) {
    //Report Trips
    val trips = mainViewModel.tripReport.collectAsState()
    var selectedTrip = mainViewModel.selectedTrip
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = device.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp))
        {
            stickyHeader {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        color = colorResource(
                            id = R.color.colorPrimary
                        ),
                        shape = RectangleShape
                    ), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Start Time", fontSize = 12.sp, color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                    Text(text = "Start Address",fontSize = 12.sp,color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                    Text(text = "End Time",fontSize = 12.sp,color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                    Text(text = "End Address",fontSize = 12.sp,color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                }
            }
            Log.d("Trips", "${trips.value.size}")
            items(trips.value) { trip ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (selectedTrip.value == trip.startTime) Color.LightGray else Color.Transparent
                    )
                    .clickable {
                        mainViewModel.onReportDetailEvent(ReportDetailEvent.ShowHighLightedTrip(trip))
                        selectedTrip.value = trip.startTime
                    }) {
                    Text(text = Utilities.getUserDateFormat(true,trip.startTime), fontSize = 12.sp,modifier = Modifier.weight(1f))
                    Text(text = trip.startAddress,fontSize = 12.sp,modifier = Modifier.weight(1f))
                    Text(text = Utilities.getUserDateFormat(true,trip.endTime),fontSize = 12.sp,modifier = Modifier.weight(1f))
                    Text(text = trip.endAddress,fontSize = 12.sp,modifier = Modifier.weight(1f))
                }
                Divider(modifier = Modifier.fillMaxWidth(),color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun showReportOptions(mainViewModel: MainViewModel,device: Device) {
    val context = LocalContext.current
    var customDateTime by remember {
        mutableStateOf("")
    }
    val dateTimePicker by lazy {
        Utilities.dateTimePicker(context,customDateTime) { newDate ->
            customDateTime = newDate
        }
    }

    val reportTypeOption = mapOf(
        "Route" to Icons.Filled.DirectionsCar,
        "Temperature" to Icons.Filled.DeviceThermostat,
        "Fuel" to Icons.Filled.LocalGasStation,
        "Event" to Icons.Filled.NotificationImportant

    )
    val reportDate = listOf("Today","Yesterday","Custom")
    var selectedReportDate by remember {
        mutableStateOf("")
    }
    var selectedReportType by remember {
        mutableStateOf("")
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    var commandTypeString by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        //Device Title Name
        Text(
            text = device.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        //Date
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick =
                {
                    selectedReportDate = "Today"
                    customDateTime = ""
                },
                modifier = Modifier
                    .widthIn(100.dp)
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    if ("Today" == selectedReportDate) colorResource(id = R.color.colorSecondary) else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp)
            )
            {
                Text(text = "Today", color = Color.White)
            }

            Button(
                onClick =
                {
                    selectedReportDate = "Yesterday"
                    customDateTime = ""
                },
                modifier = Modifier
                    .widthIn(100.dp)
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    if ("Yesterday" == selectedReportDate) colorResource(id = R.color.colorSecondary) else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp)
            )
            {
                Text(text = "Yesterday", color = Color.White)
            }
            //Custom Date Button
            Button(
                onClick =
                {
                    dateTimePicker.show()
                },
                modifier = Modifier
                    .widthIn(100.dp)
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    if ("Custom" == selectedReportDate) colorResource(id = R.color.colorSecondary) else Color.LightGray
                ),
                shape = RoundedCornerShape(20.dp)
            )
            {
                if (customDateTime.isEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Calendar",
                        tint = Color.White
                    )
                    Text(text = "Custom", color = Color.White)
                } else {
                    LaunchedEffect(Unit) {
                        selectedReportDate = "Custom"
                    }
                    Text(text = customDateTime, color = Color.White)
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .height(1.dp), color = Color.Black
        )
        //Report Options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround
        )
        {
            //Report Type
            reportTypeOption.forEach { (type, icon) ->
                ReportTypeItem(
                    text = type,
                    selectedType = selectedReportType,
                    icon = icon,
                    onSelectedType = {
                        selectedReportType = type
                    })
            }

        }
//        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp))
        {
            //Engine Cut Button
            Button(onClick = {
                openDialog = true
                commandTypeString = "Engine Cut"
            },modifier = Modifier.weight(1f)) {
                    Text(text = "Engine Cut",color = Color.White)
            }
            //Engine Restore Button
            Button(onClick = {
                openDialog = true
                commandTypeString = "Engine Restore"
            },modifier = Modifier.weight(1f)) {
                    Text(text = "Engine Restore",color = Color.White)
            }
        }
        // Report Button
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)) {
            Button(
                onClick = {
                    // TODO:: CHECK date and type is not empty string and show toast
                    val date = when (selectedReportDate) {
                        "Today" -> ReportDate.Today
                        "Yesterday" -> ReportDate.Yesterday
                        else -> ReportDate.Custom(customDateTime)
                    }
                    //might use sealedClass
                    when (selectedReportType) {
                        "Route" -> mainViewModel.onReportDetailEvent(
                            ReportDetailEvent.ReportDetailRequest(deviceId = device.id,reportDate = date, reportType = ReportType.Route)

                        )
                        "Temperature" -> mainViewModel.onReportDetailEvent(
                            ReportDetailEvent.ReportDetailRequest(deviceId = device.id,reportDate = date, reportType = ReportType.Temperature)
                        )
                        "Fuel" -> mainViewModel.onReportDetailEvent(
                            ReportDetailEvent.ReportDetailRequest(deviceId = device.id,reportDate = date, reportType = ReportType.Fuel)
                        )
                        "Event" -> mainViewModel.onReportDetailEvent(
                            ReportDetailEvent.ReportDetailRequest(deviceId = device.id, reportDate = date, reportType = ReportType.Event)
                        )
                        else -> true
                    }

                },
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.colorPrimary)),
                modifier = Modifier.weight(1f)
            )
            {
                Text(text = "Report", color = Color.White)
            }
        }

    }

    //Dialog
    MyAlertDialog(openDialog = openDialog , onCloseDialog = { openDialog = false }, typeString = commandTypeString) {
        val type = if(commandTypeString == "Engine Cut") CommandType.EngineCut else CommandType.EngineRestore
        mainViewModel.onReportDetailEvent(ReportDetailEvent.SendCommand(device = device, type = type))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showEventDetails(mainViewModel: MainViewModel,device: Device,onChangeVisibility: (Boolean) -> Unit) {
    val eventReport = mainViewModel.eventReport.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        //Device Name
        Text(
            text = device.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(vertical = 5.dp)
        )

        LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp))
        {
            stickyHeader {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(
                        color = colorResource(
                            id = R.color.colorPrimary
                        ),
                        shape = RectangleShape
                    ), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Event Type", fontSize = 12.sp, color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                    Text(text = "Time",fontSize = 12.sp,color = Color.White ,fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f))
                }
            }
//
            items(eventReport.value) { event ->
                val eventType = if (event.type != "alarm") event.type else event.attributes["alarm"]
                //Teltonika device send with eventTime( Not serverTime)
                val eventTime = event.serverTime ?: event.eventTime
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .height(30.dp)
                        .clickable {
                            // Handle Click
                            onChangeVisibility(false)
                            mainViewModel.onReportDetailEvent(ReportDetailEvent.ShowEventPoint(event.positionId))
                        }, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = eventType.toString(),fontSize = 12.sp,modifier = Modifier.weight(1f))
                    eventTime?.let {
                        Text(text = Utilities.getUserDateFormat(true,eventTime),fontSize = 12.sp,modifier = Modifier.weight(1f))
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(),color = Color.Gray, thickness = 1.dp)
            }
        }
    }
    
}

@Preview(showBackground = true)
@Composable
fun PreviewReportDetail() {
//    ReportDetail()
}