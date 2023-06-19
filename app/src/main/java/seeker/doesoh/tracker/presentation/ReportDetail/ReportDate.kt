package seeker.doesoh.tracker.presentation.ReportDetail

sealed class ReportDate {
    object Today: ReportDate()
    object Yesterday: ReportDate()
    data class Custom(val date: String): ReportDate()
}