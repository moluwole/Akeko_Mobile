package com.yung_coder.oluwole.akeko

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.adapters.LangAdapter
import com.yung_coder.oluwole.akeko.models.Models
import kotlinx.android.synthetic.main.activity_menu.*

class Menu : AppCompatActivity() {


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            list_view.visibility = if (show) View.GONE else View.VISIBLE
            list_view.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            list_view.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            loading_panel.visibility = if (show) View.VISIBLE else View.GONE
            loading_panel.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            loading_panel.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loading_panel.visibility = if (show) View.VISIBLE else View.GONE
            list_view.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        showProgress(true)
        var mList: ArrayList<Models.lang>? = ArrayList()
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        var myRef: DatabaseReference = database.getReference("lang")

        val lang_list: RecyclerView? = findViewById(R.id.list_view)
        lang_list?.adapter = null

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext, "An unexpected error occurred", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                mList?.clear()
                p0?.children?.forEach { noteSnapshot ->
                    val note = noteSnapshot.getValue(Models.lang::class.java)
                    mList?.add(note!!)
                }
                showProgress(false)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lang_list?.layoutManager = mLayoutManager
                val dividerItemDecoration = DividerItemDecoration(applicationContext, mLayoutManager.orientation)
                lang_list?.addItemDecoration(dividerItemDecoration)
                val mAdapter = LangAdapter(mList)
                lang_list?.adapter = mAdapter
            }
        })
//
//        val mLayoutManager = LinearLayoutManager(applicationContext)
//        lang_list?.layoutManager = mLayoutManager
//
//        val dividerItemDecoration = DividerItemDecoration(applicationContext, mLayoutManager.orientation)
//        lang_list?.addItemDecoration(dividerItemDecoration)
//
//        val mAdapter = LangAdapter(mList)
//        lang_list?.adapter = mAdapter
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()
    }
}

