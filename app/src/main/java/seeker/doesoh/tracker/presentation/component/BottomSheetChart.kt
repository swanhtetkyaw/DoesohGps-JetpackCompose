package seeker.doesoh.tracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.StateFlow
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.model.ChartDataset
import seeker.doesoh.tracker.data.model.Fuel
import seeker.doesoh.tracker.data.model.Temperature
import seeker.doesoh.tracker.data.remote.Device
import seeker.doesoh.tracker.presentation.Chart.ChartData
import seeker.doesoh.tracker.presentation.Chart.CustomMarker

// WHat have I DONE!!
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetChart(device: Device,sheetOpenState: State<Boolean>,chartData: LiveData<ChartData>,onClose: () -> Unit,openChart: () -> Unit) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = {
            if(it == ModalBottomSheetValue.Hidden) {
                onClose()
            }
            it != ModalBottomSheetValue.HalfExpanded
        })
//    val isSheetOpen = sheetOpenState.collectAsState()
    val charDataState = chartData.observeAsState()
    ModalBottomSheetLayout(
        sheetContent ={
            //Show body if the data exist
            if(charDataState.value != null ) {
                BottomSheetChartBody(device.name, charDataState.value!!,onClose,openChart)
            }else {
                ChartNoDataView(onClose)
            }
        },
        sheetState = bottomSheetState,
        sheetBackgroundColor = colorResource(id = R.color.colorPrimary),
        sheetContentColor = Color.Black) {
    }

    LaunchedEffect(key1 = sheetOpenState.value) {
        if(sheetOpenState.value) {
            bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
        }else {
            bottomSheetState.animateTo(ModalBottomSheetValue.Hidden)
        }

    }

}


@Composable
private fun BottomSheetChartBody(deviceName: String, chartData: ChartData, onClose: () -> Unit,openChart: () -> Unit) {
    var header: String = ""
    var date: String = ""
    var chartDataList: List<ChartDataset>? = null
        when(chartData) {
            is ChartData.ChartTemperatureData -> {
                header = "Temperature"
                date = chartData.queryDate
                chartDataList = chartData.filteredTemperatureList

            }
            is ChartData.ChartFuelData -> {
                header = "Fuel"
                date = chartData.queryDate
                chartDataList = chartData.filteredFuelList
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom)
    {
        //Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Text(text = header, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = { onClose() },
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close bottomSheet", tint = Color.White)
            }
        }
        // devicename - Date
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
        {
            Text(text = "$deviceName - $date", color = Color.White)
        }
        //Show data
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top)
        {
            when(chartData) {
                is ChartData.ChartTemperatureData -> {
                    val highest = chartData.temperatureDataset.highest.toString()
                    val lowest = chartData.temperatureDataset.lowest.toString()
                    val average = chartData.temperatureDataset.average.toString()
                    DataCard("Temperature","Highest",highest,"°C")
                    DataCard("Temperature","Average",average,"°C")
                    DataCard("Temperature","Lowest",lowest,"°C")
                }
                is ChartData.ChartFuelData -> {
                    DataCard("Fuel","Spent","30","liter")
                    DataCard("Fuel","Consumption","40","Km/L")
                    DataCard("Fuel","Filled","40","liter")
                }
            }

        }
        //Graph
       Card(
           modifier = Modifier
               .fillMaxWidth()
               .height(400.dp)
               .padding(5.dp),
           shape = RoundedCornerShape(10.dp),
           backgroundColor = colorResource(id = R.color.background_white),
           elevation = 5.dp
           )
       {
           //Draw Chart
           if(chartDataList.isNotEmpty()) {
               ChartView(chartDataList)
           }
       }
    }
}

@Composable
private fun RowScope.DataCard(type: String,info: String,dataValue: String,unit: String) {
    Card(
        modifier = Modifier
            .height(180.dp)
            .weight(1f)
            .padding(5.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = colorResource(id = R.color.background_white),
        elevation = 5.dp)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp))
        {
            Text(text = type, fontSize = 18.sp)
            Text(text = info, fontSize = 14.sp, fontWeight = FontWeight.Light, modifier = Modifier.padding(start = 20.dp))
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(10.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically)
            {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = dataValue, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = colorResource(
                        id = R.color.colorSecondary
                    ))
                    Text(text = unit, fontSize = 16.sp, color = colorResource(
                        id = R.color.colorSecondary
                    ))
                }

            }

        }
    }
}

@Composable
private fun ChartView(chartDataList: List<ChartDataset>) {
    val lineColor = colorResource(id = R.color.colorPrimary).toArgb()
    val circleIndicatorColor = colorResource(id = R.color.colorPrimaryDark).toArgb()
    val context = LocalContext.current
    AndroidView(factory = {
        LineChart(it)
    },
    update = { lineChart ->
        val entities = ArrayList<Entry>()
        chartDataList.forEachIndexed { index,value ->
           when(value) {
               is Temperature ->  entities.add(Entry((10f * index),value.temperature.toFloat()))
               is Fuel ->  entities.add(Entry((10f * index),value.fuel.toFloat()))
           }
        }
        //X axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        val label = when(chartDataList[0]) {
            is Temperature -> "Temperature"
            is Fuel -> "Fuel"
            else -> "Unknown"
        }
        //marker
        val marker = CustomMarker(chartDataList,context,R.layout.chart_markerview)
        lineChart.marker = marker
        //
        val dataSet = LineDataSet(entities,label)
        dataSet.color = lineColor
        dataSet.setDrawValues(false)
        dataSet.setDrawHighlightIndicators(true)
        dataSet.setCircleColor(circleIndicatorColor)
        dataSet.circleHoleColor = circleIndicatorColor


        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.setVisibleXRangeMaximum(200f)

        lineChart.invalidate()
    },
    modifier = Modifier.fillMaxSize())
}

@Composable
fun ChartNoDataView(onClose: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { onClose() },
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape)) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close bottomSheet", tint = Color.White)
            }
        }
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "No Data", color = Color.White, fontSize = 18.sp)
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF760F48)
@Composable
fun BottomSheetChartPreview() {
//        BottomSheetChartBody("My Device",onClose = {})
//    Row(modifier= Modifier.fillMaxSize()) {
//        DataCard("Highest","40")
//        DataCard("Average","30")
//        DataCard("Lowest","20")
//    }
}