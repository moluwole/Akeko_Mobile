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
import com.yung_coder.oluwole.akeko.R
import com.yung_coder.oluwole.akeko.Read
import com.yung_coder.oluwole.akeko.models.Models
import java.io.File

/**
 * Created by yung on 8/24/17.
 */
class BookAdapter constructor(mList: ArrayList<Models.book>?, context: Context, lang_name: String) : RecyclerView.Adapter<BookAdapter.BookViewAdapter>() {

    var mList: List<Models.book>? = null
    var generator = ColorGenerator.MATERIAL
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

    override fun onBindViewHolder(holder: BookViewAdapter?, position: Int) {
        val book_details = mList?.get(position)
        holder?.book_name?.text = book_details?.title
        val letter = book_details?.title?.get(0).toString()
        val drawable = TextDrawable.builder().buildRound(letter, generator.randomColor)
        holder?.book_thumbnail?.setImageDrawable(drawable)
        holder?.book_copyright?.text = context?.getString(R.string.source, book_details?.copyright)


        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Akekoo/Books/" + lang_name)
        if (!storageDir.exists()) storageDir.mkdirs()
        val filename = "${book_details?.title}.pdf"
        val file = File("$storageDir/$filename")
        if (!file.exists()) {
            holder?.book_download?.visibility = View.VISIBLE
        } else {
            if (file.length() > 0) {
                holder?.book_download?.visibility = View.GONE
            } else {
                file.delete()
                holder?.book_download?.visibility = View.VISIBLE
            }
        }

        holder?.book_download?.setOnClickListener {
            download(book_details?.title, storageDir.toString(), holder.book_progress, holder.book_download)
        }

        holder?.book_name?.setOnClickListener {
            val intent: Intent = Intent(context, Read::class.java)
            intent.putExtra("lang_name", lang_name)
            intent.putExtra("book_name", book_details?.title)
            intent.putExtra("page_num", book_details?.page_num)
            context?.startActivity(intent)
        }
    }

    fun download(name: String?, dirPath: String, progressbar: ProgressBar, downloadImage: ImageView) {
        val cloudinary_link = "http://res.cloudinary.com/dj4hinyoa/image/upload/w_500,h_250,c_fill/v1502564722/$name.pdf"
        val fetch = context?.let { Fetch.newInstance(it) }
        val request = Request(cloudinary_link, dirPath, "$name.pdf")
        val downloadId = fetch?.enqueue(request)

        val file = File("$dirPath/$name.pdf")
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
                if (file.exists() && file.length() > 0) {
                    Toast.makeText(context, "Download success", Toast.LENGTH_SHORT).show()
                    progressbar.visibility = View.GONE
                    downloadImage.visibility = View.GONE
                } else {
                    progressbar.visibility = View.GONE
                    downloadImage.visibility = View.VISIBLE
                }
            } else {
                progressbar.visibility = View.GONE
                downloadImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BookViewAdapter {
        val rootView = LayoutInflater.from(parent?.context).inflate(R.layout.item_material_list, parent, false)
        val view_adapter = BookViewAdapter(rootView)
        context = rootView.context
        return view_adapter
    }

    class BookViewAdapter(layoutView: View) : RecyclerView.ViewHolder(layoutView) {
        var book_name = layoutView.findViewById<TextView>(R.id.item_name)
        var book_thumbnail = layoutView.findViewById<ImageView>(R.id.item_image)
        var book_copyright = layoutView.findViewById<TextView>(R.id.item_source)
        var book_download = layoutView.findViewById<ImageView>(R.id.item_download)
        var book_progress = layoutView.findViewById<ProgressBar>(R.id.item_progressBar)
    }
}