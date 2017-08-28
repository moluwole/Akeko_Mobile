package com.yung_coder.oluwole.akeko

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.*
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.view.Menu
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.yung_coder.oluwole.akeko.curl.PageCurl
import com.yung_coder.oluwole.akeko.curl.TouchImageView
import java.io.File
import java.io.IOException
import java.util.*


class Read : AppCompatActivity() {

    companion object {
        var name = ""
        var page = 0
        var arrayList: ArrayList<String> = ArrayList()
        var exists: Boolean = false

//        var orientation = ""
    }

    class PageCurlPageTransformer : ViewPager.PageTransformer {

        override fun transformPage(page: View, position: Float) {
            if (page is PageCurl) {
                if (position > -1.0f && position < 1.0f) {
                    // hold the page steady and let the views do the work
                    page.translationX = -position * page.width
                } else {
                    page.translationX = 0.0f
                }
                if (position <= 1.0f && position >= -1.0f) {
                    (page as PageCurl).setCurlFactor(position)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        invalidateOptionsMenu()

        var intent = intent
        name = intent.getStringExtra("book_name")

        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Akekoo/Books")
        if (!storageDir.exists()) storageDir.mkdirs()
        val filename = name + ".pdf"
        val file = File(storageDir.toString() + "/" + filename)
        arrayList = ArrayList()
        if (!file.exists()) {
            page = intent.getIntExtra("page_num", 0)
            exists = false
            (1..page).mapTo(arrayList) { "http://res.cloudinary.com/dj4hinyoa/image/upload/pg_$it/v1502564722/$name.jpg" }
        } else {
            exists = true
        }

        var mCustomPagerAdapter = CustomPagerAdapter(this, file)
        var mViewPager: ViewPager = findViewById(R.id.read_pager)
        mViewPager.adapter = mCustomPagerAdapter
        mViewPager.setPageTransformer(false, PageCurlPageTransformer())


        progressBar = findViewById(R.id.progressBar2)

        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.set(Calendar.HOUR, 2)
        cal.set(Calendar.MINUTE, 0)


        val hrs = cal.timeInMillis
        val myCountDownTimer = MyCountDownTimer(hrs, 1000)
        myCountDownTimer.start()
    }

    var progressBar: ProgressBar? = null

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            val progress = (millisUntilFinished / 1000).toInt()
            progressBar?.progress = progressBar?.max!! - progress
        }

        override fun onFinish() {
            finish()
        }
    }

    class CustomPagerAdapter(context: Context?, file: File?) : PagerAdapter() {

        var context: Context? = null
        var mLayoutInflater: LayoutInflater? = null
        var file: File? = null
        private var mFileDescriptor: ParcelFileDescriptor? = null

        private var renderer: PdfRenderer? = null


        init {
            this.context = context
            mLayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            try {
                if (exists) {
                    this.file = file
                    mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        renderer = PdfRenderer(mFileDescriptor)
                        page = renderer!!.pageCount
                    } else {
                        var intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(Uri.fromFile(file), "application/pdf")
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        context?.startActivity(intent)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        override fun getCount(): Int {
            if (arrayList.size <= 0) {
                return page
            } else {
                return arrayList.size
            }
        }

        override fun isViewFromObject(view: View?, obj: Any?): Boolean {
            return view == obj as RelativeLayout
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            var itemView = mLayoutInflater?.inflate(R.layout.pager_item, container, false) as View
            var imageView: TouchImageView? = itemView.findViewById(R.id.read_imageView)

            if (exists) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val pdf_page = renderer?.openPage(position)
                    val REQ_WIDTH = pdf_page?.width
                    val REQ_HEIGHT = pdf_page?.height

                    val matrix = imageView?.imageMatrix
                    val rect = Rect(0, 0, REQ_WIDTH!!, REQ_HEIGHT!!)
//                    var bitmap: Bitmap = Bitmap()
                    val bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_8888)
                    pdf_page.render(bitmap, rect, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    imageView?.imageMatrix = matrix
                    imageView?.setImageBitmap(bitmap)
                    pdf_page.close()
                }
            } else {
                Picasso.with(context)
                        .load(arrayList[position])
                        .into(imageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                imageView?.setZoom(1.toFloat())
                            }

                            override fun onError() {
                                Toast.makeText(context, "An error occured. Try again", Toast.LENGTH_SHORT).show()
                            }
                        })
            }
            container?.addView(itemView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return itemView
        }

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(`object` as RelativeLayout)
        }
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
            }
        } else if (id == R.id.action_about) {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}

