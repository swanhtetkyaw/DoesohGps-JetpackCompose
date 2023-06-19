package seeker.doesoh.tracker.notification


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.presentation.MainActivity

class GpsEventNotification(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val DOESOH_CHANNEL_ID = "doesoh_channel_id"
        const val NOTIFICATION_ENABLE = "notification_enable"
        const val TAG = "GpsNotification"
    }

    fun showNotification(id: Int,message:String){
        val isNotificationEnable = sharedPreferences.getBoolean(NOTIFICATION_ENABLE,false)
        if(isNotificationEnable) {
            gpsNotification(id,message)
        }else {
            Log.d(TAG, " Ignore Notification")
        }
    }

    private fun gpsNotification(id: Int,message: String) {
        val title = "GPS Events"
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, DOESOH_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(sound)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id,notification)
    }
}