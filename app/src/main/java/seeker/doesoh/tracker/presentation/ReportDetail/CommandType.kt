package seeker.doesoh.tracker.presentation.ReportDetail

sealed class CommandType {
    object EngineCut: CommandType()
    object EngineRestore: CommandType()
}