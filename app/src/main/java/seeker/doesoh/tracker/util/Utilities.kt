package seeker.doesoh.tracker.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.model.DateTimeISO
import seeker.doesoh.tracker.data.remote.Position
import seeker.doesoh.tracker.data.remote.StopReport
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt


object Utilities {

    fun dateTimePicker(context: Context, date: String, onDateChange: (newDate: String)->Unit): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            context,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                onDateChange("$mDayOfMonth/${mMonth+1}/$mYear")
            }, year, month, day
        )
    }

    fun formatInfoWindowString(position: Position,speedUnit: String,distanceUnit: String,isTwelveHour: Boolean): String {
        var ignition:String? = null
        var totalDistance: String? = null
        var fuel: Int? = null
        var temperature: Double? = null
        val time = getUserDateFormat(isTwelveHour,position.fixTime)
        val address = position.address ?: (String.format("%.5f", position.latitude) + " : " + String.format("%.5f", position.longitude))
        val speed = getVehicleSpeed(position.speed,speedUnit)
        val course = getCardinal(position.course)
        if(position.attributes.containsKey("ignition")) {
             ignition = if (position.attributes["ignition"] as Boolean) "Yes" else "No"
        }

        if(position.attributes.containsKey("totalDistance")) {
            totalDistance = getVehicleDistance(position.attributes["totalDistance"] as Double,distanceUnit)
        }

        if(position.attributes.containsKey("fuel")) {
            val fuelDouble = position.attributes["fuel"] as Double
            fuel = fuelDouble.toInt()
        }

        if(position.attributes.containsKey("deviceTemp")) {
            temperature = position.attributes["deviceTemp"] as Double
//            temperature = tempDouble.toString()
        }

       val formattedString = StringBuilder()
            .appendLine("Time: $time")
            .appendLine("Address: $address")
            .appendLine("Speed: $speed")
            .appendLine("Course: $course")

        if(ignition != null) {
            formattedString.appendLine("Ignition: $ignition")
        }

        if(totalDistance != null) {
            formattedString.appendLine("TotalDistance: $totalDistance")
        }

        if(temperature != null) {
            formattedString.appendLine("Temperature: ${String.format("%.2f",temperature)} °C" )
        }

        if(fuel != null) {
            formattedString.appendLine("Fuel: $fuel liters")
        }

        return formattedString.toString()
    }

    fun formatStopInfoWindowString(stopReport: StopReport):String {
        val address = stopReport.address ?: (String.format("%.5f", stopReport.latitude) + " : " + String.format("%.5f", stopReport.longitude))
        val duration = durationFormatter(stopReport.duration)
        val startTime = getUserDateFormat(true,stopReport.startTime)
        val endTime = getUserDateFormat(true,stopReport.endTime)

        val formattedString = StringBuilder()
            .appendLine("Time: $startTime - $endTime")
            .appendLine("Duration: $duration")
            .appendLine("Address: $address")

        return formattedString.toString()
    }

    fun durationFormatter(value: Long): String {
        val hours = floor((value / 3600000).toDouble()).toInt()
        val minutes = (value % 3600000 / 60000).toFloat().roundToInt()
        return "$hours h $minutes m"
    }

    private fun getVehicleSpeed(vehicleSpeed: Double, vehicleSpeedUnit: String?): String {
        var speedUnit = "kmh"
        var factor = 1.852
        if (vehicleSpeedUnit != null) {
            when (vehicleSpeedUnit) {
                "kmh" -> {
                    speedUnit = "kmh"
                    factor = 1.852
                }
                "mph" -> {
                    speedUnit = "mph"
                    factor = 1.15078
                }
                else -> {
                    speedUnit = "kn"
                    factor = 1.0
                }
            }
        }
        val speed = vehicleSpeed * factor
        return String.format("%.1f", speed) + " " + speedUnit
    }

    fun getCardinal(course: Double): String {
        val courseValues = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        var calCourse = course
        if (calCourse > 360) {
            calCourse = course - 360
        }
        val index = Math.floor(calCourse / 45).toInt()
        return if (index <= 7) courseValues[index] else { "" }
    }

    private fun getVehicleDistance(vehicleDistance: Double, vehicleDistanceUnit: String?): String {
        val speedUnit: String
        val factor: Double
        when (vehicleDistanceUnit) {
            "km" -> {
                speedUnit = "km"
                factor = 0.001
            }
            "mi" -> {
                speedUnit = "mi"
                factor = 0.000621371192
            }
            "nmi" -> {
                speedUnit = "nmi"
                factor = 0.000539956803
            }
            else -> {
                speedUnit = "m"
                factor = 1.0
            }
        }
        val distance = vehicleDistance * factor
        return distance.toInt().toString() + " " + speedUnit
    }

    @SuppressLint("SimpleDateFormat")
    fun getUserDateFormat(isTwelveHour: Boolean,dateString: String): String {
        val ISOFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX")
        val date = ISOFormat.parse(dateString)
        val dateFormat: SimpleDateFormat = if (isTwelveHour) {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        }
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    fun getTodayDateISO(): DateTimeISO {
        val c = Calendar.getInstance()
        // 12:00 am
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0)
        val sDate = c.time
        // 11:45 pm
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 45);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0)
        val eDate = c.time
        val queryDateStringFormat = SimpleDateFormat("dd/MM/yyy")
        val queryDateString = queryDateStringFormat.format(sDate)
        Log.d("Date", "getTodayDateISO: $queryDateString")
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val startDate = isoDateFormat.format(sDate)
        val endDate = isoDateFormat.format(eDate)
        Log.d("Date", "Start: $startDate End: $endDate")
        return DateTimeISO(startDate, endDate,queryDateString)
    }

    fun getYesterdayDateISO(): DateTimeISO {
        val c = Calendar.getInstance()
        // 12:00 am
        c.add(Calendar.DATE,-1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0)
        val sDate = c.time
        // 11:45 pm
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 45);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0)
        val eDate = c.time
        Log.d("Date", "$eDate")
        Log.d("Date", "$sDate")
        val queryDateStringFormat = SimpleDateFormat("dd/MM/yyyy")
        val queryDateString = queryDateStringFormat.format(sDate)
        Log.d("Date", "getYesterdayDate: $queryDateString")
        val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val startDate = isoDateFormat.format(sDate)
        val endDate = isoDateFormat.format(eDate)
        Log.d("Date", "Start: $startDate End: $endDate")
        return DateTimeISO(startDate, endDate,queryDateString)
    }

    fun customDateISO(date: String):DateTimeISO {
        val startTime = "00:00"
        val endTime = "23:45"
        Log.d("Date", "customDateISO: $date")
        val format = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm").withZone(TimeZone.getDefault().toZoneId())
        Log.d("Date", "$date $startTime")
        Log.d("Date", "$date $endTime")
//        val startDate = LocalDate.parse(date,format)
        val start = format.parse("$date $startTime")
        val end = format.parse("$date $endTime")
        Log.d("Date", "$start")
        Log.d("Date", "$end")
        val isoDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

        val startDate = isoDateFormat.format(start)
        val endDate = isoDateFormat.format(end)

        Log.d("Date", "Start: $startDate End: $endDate")
        return DateTimeISO(startDate,endDate,date)
    }

    fun getIcon(
        status: String,
        category: String,
        position: Position
    ): BitmapDescriptor {
//        var motion: Boolean? = null
        var ignition: String? = null
        val speed = position.speed

        if(position.attributes.containsKey("ignition")) {
            ignition = if(position.attributes["ignition"] as Boolean) "Yes" else "No"
        }

//        if(position.attributes.containsKey("motion")) {
//            motion = position.attributes["motion"] as Boolean
//        }

        return when (status) {
            "online" -> {
                val isIgnition = "Yes" == ignition
                val isMoving = isIgnition && speed > 0
                when (category) {
                    "person" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_person_g else if (isIgnition) R.drawable.ic_person_o else R.drawable.ic_person_d)
                    }
                    "truck" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_truck_g else if (isIgnition) R.drawable.ic_truck_o else R.drawable.ic_truck_d)
                    }
                    "car" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_car_g else if (isIgnition) R.drawable.ic_car_o else R.drawable.ic_car_d)
                    }
                    "crane" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_crane_g else if (isIgnition) R.drawable.ic_crane_o else R.drawable.ic_crane_d)
                    }
                    "van" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_concreate_mixer_g else if (isIgnition) R.drawable.ic_concreate_mixer_o else R.drawable.ic_concreate_mixer_d)
                    }
                    "bus" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_bus_g else if (isIgnition) R.drawable.ic_bus_o else R.drawable.ic_bus_d)
                    }
                    "boat" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_boat_g else if (isIgnition) R.drawable.ic_boat_o else R.drawable.ic_boat_d)
                    }
                    "motorcycle" -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_motorcycle_g else if (isIgnition) R.drawable.ic_motorcycle_o else R.drawable.ic_default_d)
                    }
                    else -> {
                        BitmapDescriptorFactory.fromResource(if (isMoving) R.drawable.ic_default_g else if (isIgnition) R.drawable.ic_default_o else R.drawable.ic_default_d)
                    }
                }
            }
            "offline" -> {
                when (category) {
                    "person" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_person_r)
                    }
                    "truck" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_r)
                    }
                    "car" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_car_r)
                    }
                    "crane" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_crane_r)
                    }
                    "van" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_concreate_mixer_r)
                    }
                    "bus" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_r)
                    }
                    "boat" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_boat_r)
                    }
                    else -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_default_r)
                    }
                }
            }
            else -> {
                when (category) {
                    "person" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_person_b)
                    }
                    "truck" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_truck_b)
                    }
                    "car" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_car_b)
                    }
                    "crane" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_crane_b)
                    }
                    "van" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_concreate_mixer_b)
                    }
                    "bus" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_b)
                    }
                    "boat" -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_boat_b)
                    }
                    else -> {
                        BitmapDescriptorFactory.fromResource(R.drawable.ic_default_b)
                    }
                }
            }
        }
    }
}