package com.ddvader44.dankmemes

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_make_meme.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream


class MakeMemeActivity : AppCompatActivity() {

    private val MY_PERMISSION_REQUEST = 1
    private val RESULT_LOAD_IMAGE = 2

    lateinit var topText: EditText
    lateinit var bottomText: EditText
    lateinit var memeTop: TextView
    lateinit var memeBottom: TextView
    lateinit var memeImage: ImageView
    var imageLoaded = false
    var textAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_meme)
        topText = findViewById(R.id.editText1)
        bottomText = findViewById(R.id.editText2)
        memeTop = findViewById(R.id.textView1)
        memeBottom = findViewById(R.id.textView2)
        memeImage = findViewById(R.id.imageView)
        requestPermissions()
    }


    fun load(view: View) {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }
    fun clear(view: View) {
        topText.setText("")
        bottomText.setText("")
        save.isEnabled = false
        share.isEnabled = false

    }
    fun tryBtn(view: View) {}
    fun save(view: View) {}
    fun share(view: View) {}


    fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    fun storeMeme(bm: Bitmap, fileName: String?) {
        val dirPath: String =
            Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/MEME"
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dirPath, fileName)
        try {
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(this, "Meme Saved Successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
        }
    }



    // permissions

    private fun requestPermissions() {
        if (Utility.hasLocationPermissions(applicationContext)) {
            return
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept storage permissions to use this app.",
                MY_PERMISSION_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


}
