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
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.View.OnLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dialogs.EditSportsDialog
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.FileDescriptor
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {
    // ** Data
    private lateinit var profile: Profile

    // ** Profile Image
    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onCameraImageReturned(it)}
    private val launcherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onGalleryImageReturned(it)}
    private var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    private val IMAGE_CAPTURE_CODE: Int = 654
    private lateinit var imageEdit: ImageView

    // ** Sports
    private lateinit var addSportButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // ** Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // ** Profile Data
        profile = Profile.fromJSON(JSONObject(intent.getStringExtra("profile")!!))

        // ** Profile TextFields Edit
        val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.setText(profile.nickname)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)

        // TODO: Add birthday DatePicker

        // ** Profile Image
        imageEdit = findViewById(R.id.imageEdit)
        imageEdit.setOnLongClickListener{
            openCamera()
            true
        }
        imageEdit.setOnClickListener{
            openGallery()
        }
        val sportContainer = findViewById<LinearLayout>(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        profile.populateSportCards(this, sportContainer)

        // ** Add Sports Button
        addSportButton = findViewById(R.id.add_sport_button)
        addSportButton.setOnClickListener() { openSportSelectionDialog() }
    }

    private fun openGallery(){
        if (checkCameraPermission()){
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

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
        image_uri = response.data?.data

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
    private fun checkCameraPermission(): Boolean{
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 121)
            return false

        }
        return true
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
            putString("profile", profile.toJSON().toString())
            intent.putExtra("newProfile", profile.toJSON().toString())
            println("new profile is")
            println(profile.toJSON().toString())
            apply()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    /**
     * Open sport selection dialog
     */
    private fun openSportSelectionDialog() {
        val dialog = EditSportsDialog()
        dialog.show(supportFragmentManager, "edit_sports")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile_edit, menu)
        println("Menu created")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit -> {
                saveEdit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}