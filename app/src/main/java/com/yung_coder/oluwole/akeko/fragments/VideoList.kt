package com.yung_coder.oluwole.akeko.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import com.yung_coder.oluwole.akeko.R
import com.yung_coder.oluwole.akeko.adapters.VideoAdapter
import com.yung_coder.oluwole.akeko.models.Models


/**
 * A simple [Fragment] subclass.
 */
class VideoList : Fragment() {

    companion object {
        var lang_name: String? = null
        fun newInstance(extra: String): VideoList {
            val fragment = VideoList()
            lang_name = extra
            return fragment
        }
    }


    var recycler_view: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_video, container, false)
        recycler_view = rootView?.findViewById<RecyclerView>(R.id.video_list_view)
        recycler_view?.adapter = null

//        var lang_name =

        val mLayoutManager = LinearLayoutManager(rootView.context)
        recycler_view?.layoutManager = mLayoutManager
        val dividerItemDecoration = DividerItemDecoration(rootView.context, mLayoutManager.orientation)
        recycler_view?.addItemDecoration(dividerItemDecoration)

        var mList: ArrayList<Models.video>? = ArrayList()
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var myRef: DatabaseReference = database.getReference(VideoList.lang_name)

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Toast.makeText(rootView.context, "An unexpected error occurred", Toast.LENGTH_LONG).show()
                Log.e("Error", p0.toString())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                mList?.clear()
                p0?.children?.forEach { noteSnapshot ->
                    val note = noteSnapshot.getValue(Models.video::class.java)
                    if (note?.type == "videos") {
                        mList?.add(note)
                    }
                }

                if (mList?.count()!! > 0) {
                    val mLayoutManager = LinearLayoutManager(rootView.context)
                    recycler_view?.layoutManager = mLayoutManager

                    val mAdapter = VideoAdapter(mList, rootView.context, lang_name!!)
                    recycler_view?.adapter = mAdapter
                } else {
                    Toast.makeText(rootView.context, "No Video Material for $lang_name available yet", Toast.LENGTH_SHORT).show()
                }
            }
        })



        return rootView
    }

}// Required empty public constructor
