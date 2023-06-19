package seeker.doesoh.tracker.presentation.ReportDetail

sealed class ReportType {
    object Temperature: ReportType()
    object Fuel: ReportType()
    object Route: ReportType()
    object Event: ReportType()
}
