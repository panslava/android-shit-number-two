package com.example.podcasts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_upload.*


class UploadActivity : AppCompatActivity() {
    companion object {
        private var imageLoaded = false

        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;

        private val AUDIO_PICK_CODE = 1002;
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

    fun onAudioUploadButton(view: View) {
        val intentUpload = Intent()
        intentUpload.type = "audio/*"
        intentUpload.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentUpload, AUDIO_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            try {
                val imageUri = data.data!!;
                val imageStream = contentResolver.openInputStream(imageUri);
                val selectedImage = BitmapFactory.decodeStream(imageStream);

                upload_image_preview.setImageBitmap(selectedImage)
            } catch (ex: Exception) {
                println("In exception")
                Glide.with(this).load(data.data).into(upload_image_preview)

                println("exception caught")
            }
            upload_image_icon.visibility = View.INVISIBLE
            upload_image_preview.visibility = View.VISIBLE
            imageLoaded = true
        }
        if (requestCode == AUDIO_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            val filePath = selectedImage?.let { getFileName(it) }

            println("filepath")
            println(filePath)

            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(this, selectedImage)
            val durationStr =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            val millSecond = durationStr!!.toLong()
            val text = formatMilliSeccond(millSecond)

            upload_length.text = text
            upload_filename.text = filePath
            upload_center_not_uploaded.visibility = View.INVISIBLE
            upload_center_uploaded.visibility = View.VISIBLE
            upload_next_button.isClickable = true
            upload_next_button.background = ContextCompat.getDrawable(this, R.drawable.blue_button)
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor =
                contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (result != null) {
                    if (cut != null) {
                        result = result.substring(cut + 1)
                    }
                }
            }
        }
        return result
    }

    fun formatMilliSeccond(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString
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

    fun onEditClick(view: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    fun buttonOnClick(view: View) {
        val intent = Intent(this, ResultPreviewActivity::class.java)
        startActivity(intent)
    }


    fun backOnClick(view: View) {
        finish()
    }
}