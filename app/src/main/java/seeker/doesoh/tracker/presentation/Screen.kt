package seeker.doesoh.tracker.presentation

sealed class Screen (val route: String) {
    object MapScreen: Screen("map_screen")
    object ReportScreen: Screen("report_screen")
    object InsideMap: Screen("mapInside_screen")
    object LoginScreen: Screen("login_screen")
}