package seeker.doesoh.tracker.data.model

data class Temperature(
    val temperature: Int, override val date: String, override val address: String,
): ChartDataset
