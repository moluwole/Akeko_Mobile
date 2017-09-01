package com.yung_coder.oluwole.akeko

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_alarm_receiver.*
import java.io.IOException


class AlarmReceiver : AppCompatActivity() {

    val media_player: MediaPlayer? = null
    var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_receiver)
        createNotification("Alarm Schedule for the Day")
        alarm_launch.setOnClickListener {
            media_player?.stop()
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        alarm_no.setOnClickListener {
            media_player?.stop()
            finish()
        }
    }

    private fun playSound(alert: Uri?) {
        try {
            ringtone = RingtoneManager.getRingtone(applicationContext, alert)
            ringtone?.play()

        } catch (e: IOException) {
            println("OOPS")
        }

    }

    private fun createNotification(messageBody: String) {
        val intent = Intent(this, Menu::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val resultIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon)


        val notificationSoundURI = getUri()
        val mNotificationBuilder = NotificationCompat.Builder(this)
                .setBadgeIconType(R.drawable.icon)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Akekọ Alarm Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setLargeIcon(bitmap)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, mNotificationBuilder.build())
        playSound(getUri())
    }

    fun getUri(): Uri? {
        val PREFERENCE_SOUND = "alarm_sound"
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPref.contains(PREFERENCE_SOUND)) {
            val strRingtonePreference = sharedPref.getString(PREFERENCE_SOUND, "DEFAULT_SOUND")
            return Uri.parse(strRingtonePreference)
        } else {
            return getAlarmUri()
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private fun getAlarmUri(): Uri? {
        var alert: Uri? = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                if (alert == null) {
                    Toast.makeText(this, "No sound", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return alert
    }

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        if (ringtone?.isPlaying as Boolean) {
            ringtone?.stop()
        }
        super.finish()
    }
}
