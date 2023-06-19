package seeker.doesoh.tracker.presentation.LogIn

data class LogInState(
    val isLoading: Boolean = false,
    val isAuth: Boolean = false,
    val error: String = ""
)