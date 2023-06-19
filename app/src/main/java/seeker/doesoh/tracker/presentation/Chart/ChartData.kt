package seeker.doesoh.tracker.presentation.Chart

import seeker.doesoh.tracker.data.model.Fuel
import seeker.doesoh.tracker.data.model.Temperature
import seeker.doesoh.tracker.data.model.TemperatureDataset
import seeker.doesoh.tracker.presentation.ReportDetail.ReportType

sealed class ChartData {
    data class ChartTemperatureData(val filteredTemperatureList: List<Temperature>,val temperatureDataset: TemperatureDataset,val queryDate: String): ChartData()
    data class ChartFuelData(val filteredFuelList: List<Fuel>,val fuelConsumption: String,val fuelFill: String,val queryDate: String ): ChartData()
}
