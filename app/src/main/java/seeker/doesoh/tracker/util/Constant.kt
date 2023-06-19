package seeker.doesoh.tracker.util

object Constant {
    const val BASE_URL = "https://track-1.doesoh.com/"
    val BASE_URL_SHORT_LIST = listOf(
        "Track-1",
        "Track-2",
        "Track-3",
        "Track-4"
    )

    val BASE_URL_MAP = mapOf(
        "Track-1" to "https://track-1.doesoh.com/",
        "Track-2" to "https://track-2.doesoh.com/",
        "Track-3" to "https://track-3.doesoh.com/",
        "Track-4" to "https://track-4.doesoh.com/",
    )

    const val USERNAME_PREFERENCE = "username_preference"
    const val PASSWORD_PREFERENCE = "password_preference"
    const val SHORT_URL_PREFERENCE = "short_url_preference"
}