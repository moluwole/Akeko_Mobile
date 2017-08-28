package com.yung_coder.oluwole.akeko

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
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

        alarm_launch.setOnClickListener {
            media_player?.stop()
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish()
        }

        alarm_no.setOnClickListener {
            media_player?.stop()
//            moveTaskToBack(true)
            finish()
        }

        playSound(this, getUri())
    }

    private fun playSound(context: Context, alert: Uri?) {
        try {
            ringtone = RingtoneManager.getRingtone(applicationContext, alert)
            ringtone?.play()

        } catch (e: IOException) {
            println("OOPS")
        }

    }

    fun getUri(): Uri? {
        val PREFERENCE_SOUND = "alarm_sound"
        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val strRingtonePreference = sharedPref.getString(PREFERENCE_SOUND, "DEFAULT_SOUND")
        return Uri.parse(strRingtonePreference)
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
        if (ringtone?.isPlaying!!) {
            ringtone?.stop()
        }
        super.finish()
    }
}
