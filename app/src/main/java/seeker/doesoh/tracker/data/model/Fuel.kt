package seeker.doesoh.tracker.data.model

data class Fuel(
    val fuel: Int,
    override val date: String,
    override val address: String,
): ChartDataset
