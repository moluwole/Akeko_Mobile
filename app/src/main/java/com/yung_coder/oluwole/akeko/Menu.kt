package com.yung_coder.oluwole.akeko

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.adapters.LangAdapter
import com.yung_coder.oluwole.akeko.models.Models
import kotlinx.android.synthetic.main.activity_menu.*


class Menu : AppCompatActivity() {


    private fun filter(models: ArrayList<Models.lang>?, query: String): ArrayList<Models.lang> {
        val lowerCaseQuery = query.toLowerCase()

        val filteredModelList: ArrayList<Models.lang> = ArrayList()
        if (models != null) {
            for (model in models) {
                val text = model.name.toLowerCase()
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model)
                }
            }
        }
        return filteredModelList
    }

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

    var mList: ArrayList<Models.lang>? = ArrayList()
    var mAdapter: LangAdapter? = null
    var lang_list: RecyclerView? = null

    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        showProgress(true)
        if (database == null) {
            database = FirebaseDatabase.getInstance()
            database?.setPersistenceEnabled(true)
        }
        var myRef: DatabaseReference? = database?.getReference("lang")

        lang_list = findViewById(R.id.list_view)
        lang_list?.adapter = null

        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(applicationContext, "There seems to be a Network Connectivity Issue as a connection could not be established to the Akekọ Server", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                mList?.clear()
                p0?.children?.forEach { noteSnapshot ->
                    val note = noteSnapshot.getValue(Models.lang::class.java)
                    if (note != null) {
                        mList?.add(note)
                    }
                }
                showProgress(false)
                val mLayoutManager = LinearLayoutManager(applicationContext)
                lang_list?.layoutManager = mLayoutManager
                val dividerItemDecoration = DividerItemDecoration(applicationContext, mLayoutManager.orientation)
                lang_list?.addItemDecoration(dividerItemDecoration)
                mAdapter = LangAdapter(mList)
                lang_list?.adapter = mAdapter
            }
        })
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                val filteredModelList = filter(mList, query)
                mAdapter = LangAdapter(filteredModelList)
                lang_list?.adapter = mAdapter
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.action_about) {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}

