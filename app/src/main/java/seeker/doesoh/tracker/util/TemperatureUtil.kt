package seeker.doesoh.tracker.util

import android.util.Log
import seeker.doesoh.tracker.data.model.Temperature
import seeker.doesoh.tracker.data.model.TemperatureDataset

object TemperatureUtil {
    private const val TAG = "TemperatureUtil"
    fun findLowestHighestAverage(temperatureList: ArrayList<Int>): TemperatureDataset? {
        val highestTemp = temperatureList.maxOrNull()
        val lowestTemp = temperatureList.minOrNull()
        Log.d("temp", "findLowestHighestAverage: ${temperatureList.sum()}")
        val averageTemp = temperatureList.sum() / temperatureList.size

        Log.d("temp", "findLowestHighestAverage: $highestTemp : $lowestTemp : $averageTemp ")
        if(highestTemp != null && lowestTemp != null && averageTemp != null) {
            return TemperatureDataset(highest = highestTemp, lowest = lowestTemp, average = averageTemp)
        }
        return null
    }

    fun filterTemperatureList(temperatureList: List<Temperature>): ArrayList<Temperature> {
        var previousTemperature: Temperature? = null
        var currentTemperature: Temperature
        val filteredTemperature = ArrayList<Temperature>()
        temperatureList.forEachIndexed { index, temperature ->
            if(index == 0) {
                previousTemperature = temperature
                filteredTemperature.add(temperature)
                return@forEachIndexed
            }
            currentTemperature = temperature
            previousTemperature?.let { previousTemp ->
                if(previousTemp.temperature == currentTemperature.temperature) return@forEachIndexed
                else {
                    previousTemperature = currentTemperature
                    filteredTemperature.add(currentTemperature)

                }
            }
        }

        Log.d(TAG, "filterTemperatureList: ${filteredTemperature.size}")
        filteredTemperature.forEach { temp ->
            Log.d(TAG, "filterTemperatureList: ${temp.temperature} ")
        }
        return filteredTemperature
    }

}