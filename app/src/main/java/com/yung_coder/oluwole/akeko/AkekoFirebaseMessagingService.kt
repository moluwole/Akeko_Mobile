package com.yung_coder.oluwole.akeko

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AkekoFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "Akekọ"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //create notification
        val pref = getPref()
        if (pref) {
            remoteMessage.notification.body?.let { createNotification(it) }
        }
    }

    fun getPref(): Boolean {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(this)
        val pref_val = prefManager.getBoolean("notify_switch", true)
        return pref_val
    }

    private fun getAlarmUri(): Uri? {
        var alert: Uri? = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
        }
        return alert
    }

    private fun createNotification(messageBody: String) {
        val intent = Intent(this, Menu::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon)

        val notificationSoundURI = getAlarmUri()
        val mNotificationBuilder = NotificationCompat.Builder(this)
                .setBadgeIconType(R.drawable.icon)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Akekọ Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent)
                .setLargeIcon(bitmap)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mNotificationBuilder.build())
    }
}
