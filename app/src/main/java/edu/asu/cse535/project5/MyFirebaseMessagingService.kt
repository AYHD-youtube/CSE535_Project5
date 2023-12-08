package edu.asu.cse535.project5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channel_id = "notification_channel"
const val channelName = "edu.asu.cse535.project5"

class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            generateMessage(message.notification?.title ?: "", message.notification?.body ?: "")
        }
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("edu.asu.cse535.project5", R.layout.layout_notification)
        remoteView.setTextViewText(R.id.notiTv, title)
        remoteView.setTextViewText(R.id.notiDescription, message)
        remoteView.setImageViewResource(R.id.notiIv, R.drawable.ic_launcher_background)
        return remoteView
    }

    fun generateMessage(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivities(
            this, 0, arrayOf(intent),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationLayout = RemoteViews(packageName, R.layout.layout_notification_collapsed)
        notificationLayout.setTextViewText(R.id.notiTv, title)
        val builder = NotificationCompat.Builder(
            applicationContext,
            channel_id
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(getRemoteView(title, message))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel =
            NotificationChannel(channel_id, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, builder.build())
    }


}