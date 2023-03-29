package it.polito.mad.buddybench.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.FileDescriptor
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {
    lateinit var profile: Profile
    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onCameraImageReturned(it)}
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onGalleryImageReturned(it)}

    private var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    private val IMAGE_CAPTURE_CODE: Int = 654
    lateinit var imageEdit: ImageView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        profile = Profile.fromJSON(JSONObject(intent.getStringExtra("profile")!!))
        val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.setText(profile.nickname)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)

        imageEdit = findViewById<ImageView>(R.id.imageEdit)
        imageEdit.setOnLongClickListener{
            openCamera()
            true
        }
        imageEdit.setOnClickListener{
            openGallery()
        }

        /*
        imageEdit.setOnClickListener{
            OnLongClickListener{
                openGallery()
                true
            }
        }*/

    }

    private fun openGallery(){
        if (checkCameraPermission()){
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.putExtra("requestCode", RESULT_LOAD_IMAGE)
            launcherGallery.launch(galleryIntent)
        }
    }
    private fun openCamera() {
        if (checkCameraPermission()){
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            val bundle = Bundle()
            bundle.putInt("requestCode", IMAGE_CAPTURE_CODE)
            cameraIntent.putExtras(bundle)
            launcherCamera.launch(cameraIntent)
        }
    }

    private fun onCameraImageReturned(response: androidx.activity.result.ActivityResult) {

        if (response.resultCode != Activity.RESULT_OK) return

        val bitmap = uriToBitmap(image_uri!!)
        imageEdit.setImageBitmap(bitmap)



    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {

        if (response.resultCode != Activity.RESULT_OK) return

        val bitmap = uriToBitmap(image_uri!!)
        imageEdit.setImageBitmap(bitmap)



    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    fun checkCameraPermission(): Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 121)
                return false

            }
            return true
        }
        return false
    }

    private fun saveEdit(){
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
            val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
            val localityEdit = findViewById<EditText>(R.id.localityEdit)

            profile.fullName = fullNameEdit.text.toString()
            profile.nickname = nicknameEdit.text.toString()
            profile.location = localityEdit.text.toString()
            profile.imageUri = image_uri
            println(image_uri)
            putString("profile", profile.toJSON().toString())

            apply()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveEdit()
    }

}