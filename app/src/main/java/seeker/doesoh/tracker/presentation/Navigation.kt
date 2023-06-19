package seeker.doesoh.tracker.presentation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import seeker.doesoh.tracker.presentation.screen.LogInScreen
import seeker.doesoh.tracker.presentation.screen.MapScreen
import seeker.doesoh.tracker.presentation.screen.ReportScreen


@Composable
fun Navigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val isAuth = mainViewModel.logInState.value.isAuth
    val initialRoute = if (isAuth) Screen.MapScreen.route else Screen.LoginScreen.route
    NavHost(navController = navController, startDestination = initialRoute) {
        composable(route = Screen.LoginScreen.route) {
            LogInScreen(mainViewModel = mainViewModel, navController = navController)
        }
        composable(route = Screen.MapScreen.route) {
            MapScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = Screen.ReportScreen.route) {
            ReportScreen(navController= navController,mainViewModel = mainViewModel)
        }

    }

}