package eco.com.gurunanak.utlity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


import org.json.JSONException
import org.json.JSONObject
import android.app.NotificationChannel
import android.graphics.Color
import eco.com.gurunanak.ActivitySplash
import eco.com.gurunanak.R


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {



    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e(TAG, "From: " + remoteMessage!!.toString()!!)

        if (remoteMessage == null)
            return

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.e(TAG+remoteMessage.notification!!.title!!, "Notification Body: " + remoteMessage.notification!!.body!!)
            handleNotification(remoteMessage.notification!!.body,remoteMessage.notification!!.title!!)
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())

        }
    }

    private fun handleNotification(message: String?,title:String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = "id_product"
            // The user-visible name of the channel.
            val name = "Product"
            // The user-visible description of the channel.
            val description = "Notifications regarding our products"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED
            notificationManager.createNotificationChannel(mChannel)

            val intent1 = Intent(applicationContext, ActivitySplash::class.java)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, "id_product")
                    .setSmallIcon(R.drawable.ic_stat_550) //your app icon
                    .setBadgeIconType(R.drawable.ic_stat_550) //your app icon
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setAutoCancel(true).setContentIntent(pendingIntent)
                    .setNumber(1)
                    .setColor(255)
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())
            notificationManager.notify(1000 /* ID of notification */, notificationBuilder.build())
        } else {
            val intent = Intent(this, ActivitySplash::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                    .setContentText(message)
                    .setTicker(title)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_stat_550)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(1000 /* ID of notification */, notificationBuilder.build())
        }

    }


    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }
}
