package com.ddvader44.dankmemes

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class MakeMemeActivity : AppCompatActivity() {

    private val MY_PERMISSION_REQUEST = 1
    private val RESULT_LOAD_IMAGE = 2
    lateinit var chooseImage : Button
    lateinit var topText: EditText
    lateinit var bottomText: EditText
    lateinit var memeTop: TextView
    lateinit var memeBottom: TextView
    lateinit var memeImage: ImageView
    lateinit var memes : View
    var currentImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_meme)
        topText = findViewById(R.id.editText1)
        bottomText = findViewById(R.id.editText2)
        memeTop = findViewById(R.id.textView1)
        memeBottom = findViewById(R.id.textView2)
        memeImage = findViewById(R.id.imageView)
        chooseImage = findViewById(R.id.load)
        chooseImage.setOnClickListener {
            load()
        }
        requestPermissions()
    }


    private fun load() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RESULT_LOAD_IMAGE && resultCode === RESULT_OK && data != null && data.data !=null) {
            val selectedPhotoUri = data.data
            try {
                selectedPhotoUri?.let {
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedPhotoUri
                        )
                        memeImage.setImageBitmap(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        memeImage.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }




            /*      val selectedImage: Uri? = data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(
                selectedImage!!,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()
            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor.close()
            memeImage.setImageBitmap(BitmapFactory.decodeFile(picturePath)) */


    }




    fun clear(view: View) {
        topText.setText("")
        bottomText.setText("")
        memeTop.setText("")
        memeBottom.setText("")
    }

    fun tryBtn(view: View) {
        if (topText.text.toString().equals("") && bottomText.text.toString().equals("")) {
            Toast.makeText(this, "Enter some text first", Toast.LENGTH_SHORT).show()
        } else {
            memeTop.text = topText.text.toString()
            memeBottom.text = bottomText.text.toString()
        }
    }

    fun save(view: View) {
        memes = findViewById(R.id.lay)
        val bitmap: Bitmap = screenShot(memes)
        currentImage = "meme" + System.currentTimeMillis()+".png"
        storeMeme(bitmap, currentImage)
    }


    fun share(view: View) {
        val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/MEME"
        val imageName = "meme" + System.currentTimeMillis() + ".png"
        memes = findViewById(R.id.lay)
        val bitmap: Bitmap = screenShot(memes)
        val imageFile = File(dirPath, imageName)
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.ddvader44.dankmemes.provider",
            imageFile
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"

        try {
            startActivity(Intent.createChooser(intent, "Share Via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No sharing app found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun storeMeme(bm: Bitmap, fileName: String) {
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



}
