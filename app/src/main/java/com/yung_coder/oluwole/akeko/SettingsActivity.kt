package com.yung_coder.oluwole.akeko

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.view.MenuItem
import android.widget.Toast
import java.util.*


@Suppress("DEPRECATION")
/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    companion object {
        val alarm: String = "show_alarm"
        val alarm_value: String = "alarm_interval"
        val ALARM_REQUEST_CODE = 9245
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        val prefManager = preferenceManager.sharedPreferences

        val checkAlarm: CheckBoxPreference = preferenceManager.findPreference(alarm) as CheckBoxPreference
        checkAlarm.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, p1 ->
            //            deleteAlarm()
            if (p1 is Boolean) {
                if (p1 == true) {
                    val interval = prefManager.getString(alarm_value, "")
                    setAlarm(interval)
                } else {
                    deleteAlarm()
                }
            }
            true
        }

        val valAlarm: ListPreference = preferenceManager.findPreference(alarm_value) as ListPreference
        valAlarm.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, p1 ->
            if (p1 is String) {
                val interval = p1
                setAlarm(interval)
            }
            true
        }

    }

    fun setAlarm(interval: String) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 8)
        cal.set(Calendar.MINUTE, 30)

        var alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val alarmIntent = PendingIntent.getActivity(applicationContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, time_duration(interval) * 1000, alarmIntent)
        Toast.makeText(applicationContext, "Alarm Set to $interval starting from 8:30 AM every day", Toast.LENGTH_SHORT).show()
    }

    fun deleteAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)

    }

    fun time_duration(pref_value: String): Long {
        var return_val: Long = AlarmManager.INTERVAL_HALF_DAY
        when (pref_value) {
            "One minute" -> return_val = 60
            "Every Day" -> return_val = 24 * 60 * 60
            "Every 6 hours" -> return_val = 6 * 60 * 60
            "Twice a day" -> return_val = 12 * 60 * 60
            "Thrice a day" -> return_val = 8 * 60 * 60
        }
        return return_val
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        addPreferencesFromResource(R.xml.pref_settings)
//        PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
