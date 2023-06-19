package seeker.doesoh.tracker.presentation.LogIn

sealed class LogInEvent {
    data class Authenticate(val email: String,val password: String,val url: String,val shortUrl: String): LogInEvent()
}
