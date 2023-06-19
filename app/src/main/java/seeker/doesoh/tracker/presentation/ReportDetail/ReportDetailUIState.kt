package seeker.doesoh.tracker.presentation.ReportDetail

//TODO :: add report type
data class ReportDetailUIState (
    val isLoading: Boolean = false,
    val showReports: Boolean = true,
    val showRoutes: Boolean = false,
    val showEvents: Boolean = false,
    val minimize: Boolean = false,
    val showTemperature: Boolean = false,
    val showFuel: Boolean = false
    )

