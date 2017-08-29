package com.yung_coder.oluwole.akeko

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AkekoFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "Akekọ"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.from)
        Log.d(TAG, "Notification Message Body: " + remoteMessage.notification.body!!)
        //create notification
        val pref = getPref()
        if (pref) {
            createNotification(remoteMessage.notification.body!!)
        }
    }

    fun getPref(): Boolean {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        val pref_val = prefManager.getBoolean("notify_switch", true)
        return pref_val
    }

    private fun createNotification(messageBody: String) {
        val intent = Intent(this, Menu::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationBuilder = NotificationCompat.Builder(this)
                .setBadgeIconType(R.drawable.icon)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Akekọ Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mNotificationBuilder.build())
    }
}
