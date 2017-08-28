package com.yung_coder.oluwole.akeko

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.halilibo.bettervideoplayer.BetterVideoPlayer

class Play : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val player = findViewById<BetterVideoPlayer>(R.id.player)
        val uri = Uri.parse(intent.getStringExtra("video_path"))
        player.enableControls()
        player.enableSwipeGestures()
        player.setSource(uri)
        player.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_read, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menu?.findItem(R.id.action_orientation)?.title = getString(R.string.change_orientation, "Portrait")
        } else {
            menu?.findItem(R.id.action_orientation)?.title = getString(R.string.change_orientation, "Landscape")
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if (id == R.id.action_orientation) {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                var actionBar = actionBar
                actionBar.hide()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
