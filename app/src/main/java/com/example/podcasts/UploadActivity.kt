package com.example.podcasts

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.File
import java.io.FileNotFoundException


class UploadActivity : AppCompatActivity() {
    companion object {
        private var imageLoaded = false

        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

//        upload_image_preview.clipToOutline = true
        upload_image_preview.visibility = View.INVISIBLE

        val imageUploadOverlay = findViewById<ConstraintLayout>(R.id.upload_image_overlay)
        imageUploadOverlay.isClickable = true
        imageUploadOverlay.setOnClickListener {
            if (!imageLoaded) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    startPickingImage()
                }
            } else {
                removeImage()
            }


        }
    }

    private fun removeImage() {
        upload_image_preview.setImageDrawable(null)
        upload_image_preview.visibility = View.INVISIBLE
        upload_image_icon.visibility = View.VISIBLE
        imageLoaded = false
    }

    private fun startPickingImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"),
            IMAGE_PICK_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!
            println(data.data)
            println(selectedImageUri)
            val selectedImagePath = getPath(selectedImageUri)
            print(selectedImagePath)
            val mImagePath = selectedImagePath
            val photo = getPreview(selectedImagePath)
            upload_image_preview.setImageBitmap(photo)
//            try {
//                Glide.with(this).load(data.data).into(upload_image_preview)
//                upload_image_preview.setImageURI(data.data)
//            }
//            catch (ex: FileNotFoundException) {
//                println("exception caught")
//            }
            upload_image_icon.visibility = View.INVISIBLE
            upload_image_preview.visibility = View.VISIBLE
            imageLoaded = true
        }
    }

    fun getPath(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun getPreview(fileName: String?): Bitmap? {
        val image = File(fileName)
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.getPath(), bounds)
        if (bounds.outWidth == -1 || bounds.outHeight == -1) {
            return null
        }
        val originalSize =
            if (bounds.outHeight > bounds.outWidth) bounds.outHeight else bounds.outWidth
        val opts = BitmapFactory.Options()
        opts.inSampleSize = originalSize / 64
        return BitmapFactory.decodeFile(image.getPath(), opts)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPickingImage()
        }
    }

    fun backOnClick(view: View) {
        finish()
    }
}