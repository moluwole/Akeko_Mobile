package com.yung_coder.oluwole.akeko

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.adapters.LangAdapter
import com.yung_coder.oluwole.akeko.models.Models

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        var mList: ArrayList<Models.lang>? = ArrayList()
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        var myRef: DatabaseReference = database.getReference("lang")

        val lang_list: RecyclerView? = findViewById(R.id.list_view)
        lang_list?.adapter = null

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext, "An unexpected error occurred", Toast.LENGTH_LONG).show()
                Log.e("Error", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                mList?.clear()
                p0?.children?.forEach { noteSnapshot ->
                    val note = noteSnapshot.getValue(Models.lang::class.java)
                    mList?.add(note!!)
                }

                val mLayoutManager = LinearLayoutManager(applicationContext)
                lang_list?.layoutManager = mLayoutManager
                val dividerItemDecoration = DividerItemDecoration(applicationContext, mLayoutManager.orientation)
                lang_list?.addItemDecoration(dividerItemDecoration)
                val mAdapter = LangAdapter(mList)
                lang_list?.adapter = mAdapter
            }
        })

        val mLayoutManager = LinearLayoutManager(applicationContext)
        lang_list?.layoutManager = mLayoutManager

        val dividerItemDecoration = DividerItemDecoration(applicationContext, mLayoutManager.orientation)
        lang_list?.addItemDecoration(dividerItemDecoration)

        val mAdapter = LangAdapter(mList)
        lang_list?.adapter = mAdapter
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()
    }
}

