package it.polito.mad.buddybench.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.util.LocaleData
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dialogs.EditSportsDialog
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateString
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateLocalDate
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.changeColor
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.changeColorDate

class EditProfileActivity : AppCompatActivity() {
    // ** Data
    private lateinit var profile: Profile

    // ** Profile Image
    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onCameraImageReturned(it)
        }
    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onGalleryImageReturned(it)
        }
    private var image_uri: Uri? = null
    private val RESULT_LOAD_IMAGE = 123
    private val IMAGE_CAPTURE_CODE: Int = 654
    private lateinit var imageEdit: ImageView

    // ** Sports
    private lateinit var addSportButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
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
        fullNameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(fullNameEdit, text.toString(), resources)
        }
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nicknameEdit, text.toString(), resources)

        }
        nicknameEdit.setText(profile.nickname)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)
        localityEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(localityEdit, text.toString(), resources)

        }

        val birthdayEdit = findViewById<EditText>(R.id.birthdayEdit)
        birthdayEdit.setText(profile.birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        birthdayEdit.doOnTextChanged { text, _, _, _ ->
            changeColorDate(birthdayEdit, text.toString(), resources)
        }
        // TODO: Add birthday DatePicker

        // ** Profile Image
        imageEdit = findViewById(R.id.imageEdit)
        if (profile.imageUri != null) {
            try {
                imageEdit.setImageURI(profile.imageUri)

            } catch (_: java.lang.Exception) {
                /*maybe the image has been deleted
                * retrieving the view we restore the default image
                * otherwise blank one will appear*/
                imageEdit = findViewById(R.id.imageView)

            }
        }
        imageEdit.setOnLongClickListener {
            openCamera()
            true
        }
        imageEdit.setOnClickListener {
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

    private fun openGallery() {
        if (checkCameraPermission()) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            launcherGallery.launch(galleryIntent)
        }
    }

    private fun openCamera() {
        if (checkCameraPermission()) {
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
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, image_uri!!)
        imageEdit.setImageBitmap(bitmap)
    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode != Activity.RESULT_OK) return
        image_uri = response.data?.data
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, image_uri!!)
        imageEdit.setImageBitmap(bitmap)
    }


    private fun checkCameraPermission(): Boolean {
        println("Jacopo e' bello")
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            println("Jacopo e' stupendo")
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 121)
            return false
        }
        return true
    }

    private fun formValidation(): Boolean {
        val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        val birthdayEdit = findViewById<EditText>(R.id.birthdayEdit)

        if (!validateString(fullNameEdit.text.toString())) {
            return false
        }
        if (!validateString(nicknameEdit.text.toString())) {
            return false
        }
        if (!validateString(localityEdit.text.toString())) {
            return false
        }
        if (!validateLocalDate(birthdayEdit.text.toString())) {
            return false
        }

        return true
    }

    private fun saveEdit() {
        val sharedPref: SharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
            val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
            val localityEdit = findViewById<EditText>(R.id.localityEdit)
            val birthdayEdit = findViewById<EditText>(R.id.birthdayEdit)
            profile.fullName = fullNameEdit.text.toString()
            profile.nickname = nicknameEdit.text.toString()
            profile.location = localityEdit.text.toString()
            profile.birthday = LocalDate.parse(
                findViewById<EditText>(R.id.birthdayEdit).text.toString(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )

            if (image_uri != null) {
                try {
                    profile.imageUri = BitmapUtils.saveToInternalStorage(
                        applicationContext,
                        BitmapUtils.uriToBitmap(contentResolver, image_uri!!)!!
                    )!!
                    println("Setting imageUri")
                } catch (_: IOException) {

                }
            }
            val newProfileJSON = profile.toJSON().toString()
            putString("profile", newProfileJSON)
            intent.putExtra("newProfile", newProfileJSON)
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

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit -> {
                if (!formValidation()) {
                    val toast = Toast.makeText(
                        this,
                        resources.getText(R.string.errorOnEdit),
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    return false
                }
                saveEdit()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}