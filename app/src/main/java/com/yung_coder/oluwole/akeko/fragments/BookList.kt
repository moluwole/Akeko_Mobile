package com.yung_coder.oluwole.akeko.fragments


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.R
import com.yung_coder.oluwole.akeko.adapters.BookAdapter
import com.yung_coder.oluwole.akeko.models.Models


/**
 * A simple [Fragment] subclass.
 */
class BookList : Fragment() {


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            recycler_view?.visibility = if (show) View.GONE else View.VISIBLE
            recycler_view?.animate()
                    ?.setDuration(shortAnimTime)
                    ?.alpha((if (show) 0 else 1).toFloat())
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            recycler_view?.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            loading_panel?.visibility = if (show) View.VISIBLE else View.GONE
            loading_panel?.animate()
                    ?.setDuration(shortAnimTime)
                    ?.alpha((if (show) 1 else 0).toFloat())
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            loading_panel?.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            loading_panel?.visibility = if (show) View.VISIBLE else View.GONE
            recycler_view?.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    companion object {
        var lang_name: String? = null
        var data: ArrayList<Models.book> = ArrayList()
        fun newInstance(extra: String): BookList {
            val fragment = BookList()
            lang_name = extra
            return fragment
        }
    }

    var recycler_view: RecyclerView? = null
    var loading_panel: RelativeLayout? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val rootView = inflater!!.inflate(R.layout.fragment_book, container, false)
        recycler_view = rootView?.findViewById<RecyclerView>(R.id.book_list_view)
        loading_panel = rootView?.findViewById(R.id.book_loading_panel)
        recycler_view?.adapter = null
//        var lang_name =

        val mLayoutManager = LinearLayoutManager(rootView.context)
        recycler_view?.layoutManager = mLayoutManager
        val dividerItemDecoration = DividerItemDecoration(rootView.context, mLayoutManager.orientation)
        recycler_view?.addItemDecoration(dividerItemDecoration)

        showProgress(true)
        var mList: ArrayList<Models.book>? = ArrayList()
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference(lang_name)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                showProgress(false)
                Toast.makeText(rootView.context, "An unexpected error occurred", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                showProgress(true)
                mList?.clear()
                p0?.children?.forEach { noteSnapshot ->
                    val note = noteSnapshot.getValue(Models.book::class.java)
                    if (note?.type == "books") {
                        mList?.add(note)
                    }
                }

                showProgress(false)
                
                if (mList?.count()!! > 0) {
                    val mLayoutManager = LinearLayoutManager(rootView.context)
                    recycler_view?.layoutManager = mLayoutManager

                    val mAdapter = BookAdapter(mList, rootView.context, lang_name!!)
                    recycler_view?.adapter = mAdapter
                } else {
                    Toast.makeText(rootView.context, "No Books Available for $lang_name Yet", Toast.LENGTH_SHORT).show()
                }
            }
        })
        return rootView
    }
}
