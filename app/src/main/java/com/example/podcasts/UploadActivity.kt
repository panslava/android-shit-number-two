package com.example.podcasts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_upload.*

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

        upload_image_preview.clipToOutline = true
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
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            upload_image_preview.setImageURI(data.data)
            upload_image_icon.visibility = View.INVISIBLE
            upload_image_preview.visibility = View.VISIBLE
            imageLoaded = true
        }
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