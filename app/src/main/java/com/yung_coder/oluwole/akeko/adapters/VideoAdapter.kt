package com.yung_coder.oluwole.akeko.adapters

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.tonyodev.fetch.Fetch
import com.tonyodev.fetch.request.Request
import com.yung_coder.oluwole.akeko.Play
import com.yung_coder.oluwole.akeko.R
import com.yung_coder.oluwole.akeko.models.Models
import java.io.File

/**
 * Created by yung on 8/27/17.
 */
class VideoAdapter constructor(mList: ArrayList<Models.video>?, context: Context, lang_name: String) : RecyclerView.Adapter<VideoAdapter.VideoViewAdapter>() {

    var mList: List<Models.video>? = null
    var generator: ColorGenerator? = ColorGenerator.MATERIAL
    var context: Context? = null
    var lang_name = ""

    init {
        this.mList = mList
        this.context = context
        this.lang_name = lang_name
    }

    override fun getItemCount(): Int {
        return mList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: VideoAdapter.VideoViewAdapter?, position: Int) {
        val video_details = mList?.get(position)
        holder?.video_name?.text = video_details?.title
        val letter = video_details?.title?.get(0).toString()
        val drawable = generator?.randomColor?.let { TextDrawable.builder().buildRound(letter, it) }
        holder?.video_thumbnail?.setImageDrawable(drawable)
        holder?.video_copyright?.text = context?.getString(R.string.source, video_details?.copyright)

        var vid_path = ""
        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Akekoo/Videos/" + lang_name)
        if (!storageDir.exists()) storageDir.mkdirs()
        val filename = video_details?.title + ".mp4"
        val file = File(storageDir.toString() + "/" + filename)
        if (!file.exists()) {
            holder?.video_download?.visibility = View.VISIBLE
            vid_path = "http://res.cloudinary.com/dj4hinyoa/video/upload/v1502564722/${video_details?.title}.mp4"
        } else {
            holder?.video_download?.visibility = View.GONE
            vid_path = file.toString()
        }


        holder?.video_download?.setOnClickListener {
            download(video_details?.title, storageDir.toString(), holder.video_progress, holder.video_download)
        }

        holder?.video_name?.setOnClickListener {
            val intent: Intent = Intent(context, Play::class.java)
            intent.putExtra("video_path", vid_path)
            context?.startActivity(intent)
        }
    }

    fun download(name: String?, dirPath: String, progressbar: ProgressBar, downloadImage: ImageView) {
        val cloudinary_link = "http://res.cloudinary.com/dj4hinyoa/video/upload/v1502564722/$name.mp4"
        val fetch = context?.let { Fetch.newInstance(it) }
        val request = Request(cloudinary_link, dirPath, "$name.pdf")
        val downloadId = fetch?.enqueue(request)

        progressbar.visibility = View.VISIBLE
        downloadImage.visibility = View.GONE

        fetch?.addFetchListener { id, status, progress, _, _, error ->
            progressbar.visibility = View.VISIBLE
            if (downloadId == id && status == Fetch.STATUS_DOWNLOADING) {
                progressbar.progress = progress
            } else if (error != Fetch.NO_ERROR) {
                downloadImage.visibility = View.VISIBLE
                Toast.makeText(context, "There seems to be a Network Connectivity Issue as an error occurred during download", Toast.LENGTH_SHORT).show()
            }
            if (status == Fetch.STATUS_DONE && error == Fetch.NO_ERROR) {
                Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show()
                progressbar.visibility = View.GONE
                downloadImage.visibility = View.GONE
            } else {
                progressbar.visibility = View.GONE
                downloadImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VideoAdapter.VideoViewAdapter {
        val rootView = LayoutInflater.from(parent?.context).inflate(R.layout.item_material_list, parent, false)
        val view_adapter = VideoAdapter.VideoViewAdapter(rootView)
        context = rootView.context
        return view_adapter
    }

    class VideoViewAdapter(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var video_name: TextView = layoutView.findViewById<TextView>(R.id.item_name)
        var video_thumbnail: ImageView = layoutView.findViewById<ImageView>(R.id.item_image)
        var video_copyright: TextView = layoutView.findViewById<TextView>(R.id.item_source)
        var video_download: ImageView = layoutView.findViewById<ImageView>(R.id.item_download)
        var video_progress: ProgressBar = layoutView.findViewById<ProgressBar>(R.id.item_progressBar)
    }
}