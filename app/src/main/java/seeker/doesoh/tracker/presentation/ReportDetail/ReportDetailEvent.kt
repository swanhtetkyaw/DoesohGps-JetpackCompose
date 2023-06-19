package seeker.doesoh.tracker.presentation.ReportDetail

import seeker.doesoh.tracker.data.remote.Device
import seeker.doesoh.tracker.data.remote.TripReport

sealed class ReportDetailEvent{
    object Close : ReportDetailEvent()
    object Back: ReportDetailEvent()
    object Minimize: ReportDetailEvent()
    data class ShowEventPoint(val positionId: Long): ReportDetailEvent()
    data class ShowHighLightedTrip(val tripReport: TripReport): ReportDetailEvent()
    data class RequestRoute(val deviceId: Long, val reportDate: ReportDate): ReportDetailEvent()
    data class RequestEvent(val deviceId: Long, val reportDate: ReportDate): ReportDetailEvent()
    data class RequestTemperature(val deviceId: Long, val reportDate: ReportDate): ReportDetailEvent()
    data class RequestFuel(val deviceId: Long, val reportDate: ReportDate): ReportDetailEvent()
    data class ReportDetailRequest(val deviceId: Long,val reportDate: ReportDate,val reportType: ReportType): ReportDetailEvent()
    data class SendCommand(val device: Device, val type: CommandType): ReportDetailEvent()

}