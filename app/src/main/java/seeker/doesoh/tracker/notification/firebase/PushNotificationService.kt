package seeker.doesoh.tracker.notification.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import seeker.doesoh.tracker.DoeSohApplication
import seeker.doesoh.tracker.notification.GpsEventNotification
import javax.inject.Inject
import kotlin.random.Random

const val TAG = "Notification"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {
    @Inject
    lateinit var gpsEventNotification: GpsEventNotification
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "${message.notification?.title} ")
        Log.d(TAG, "${message.notification?.body}: ")
        val msg = message.notification?.body
        val notificationId = Random.nextInt()
        //TODO:: Generate Random Id
        msg?.let {
          gpsEventNotification.showNotification(notificationId,msg)
        }

    }
}