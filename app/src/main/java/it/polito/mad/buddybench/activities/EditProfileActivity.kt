package it.polito.mad.buddybench.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import androidx.core.view.allViews
import androidx.core.widget.doOnTextChanged
import com.squareup.picasso.Picasso
import androidx.fragment.app.DialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
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
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports

class EditProfileActivity : AppCompatActivity(), EditSportsDialog.NoticeDialogListener {
    // ** Data
    private lateinit var profile: Profile
    private lateinit var datePicker: DatePickerDialog

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
    private lateinit var sportContainer: LinearLayout

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

        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        emailEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(emailEdit, text.toString(), resources)
        }
        emailEdit.setText(profile.email)

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

        val birthdayButtonEdit = findViewById<Button>(R.id.birthdayEditButton)
        birthdayButtonEdit.setText(profile.birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        // TODO: Add birthday DatePicker

        // ** Profile Image
        imageEdit = findViewById(R.id.imageEdit)
        Picasso.with(applicationContext).load("file://${profile.imageUri}").placeholder(R.drawable.person).into(imageEdit)

        imageEdit.setOnLongClickListener {
            openCamera()
            true
        }
        imageEdit.setOnClickListener {
            openGallery()
        }
        sportContainer = findViewById<LinearLayout>(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        profile.populateSportCardsEdit(this, sportContainer, onDeleteSport = { saveEdit() })

        // ** Add Sports Button
        addSportButton = findViewById(R.id.add_sport_button)
        addSportButton.setOnClickListener() { openSportSelectionDialog() }
        checkCameraPermission()
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
        Picasso.with(applicationContext).load("file://${profile.imageUri}").placeholder(R.drawable.person).into(imageEdit)
    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode != Activity.RESULT_OK) return
        image_uri = response.data?.data
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, image_uri!!)
        imageEdit.setImageBitmap(bitmap)
        Picasso.with(applicationContext).load("file://${profile.imageUri}").placeholder(R.drawable.person).into(imageEdit)

    }


    private fun checkCameraPermission(): Boolean {

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
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
        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        val birthdayEdit = findViewById<EditText>(R.id.birthdayEdit)

        if (!validateString(fullNameEdit.text.toString())) {
            return false
        }
        if (!validateString(nicknameEdit.text.toString())) {
            return false
        }
        if (!validateString(emailEdit.text.toString())) {
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
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            val sportContainer = findViewById<LinearLayout>(R.id.sportsContainerEdit)

            val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
            val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
            val localityEdit = findViewById<EditText>(R.id.localityEdit)
            val birthdayEdit = findViewById<EditText>(R.id.birthdayEdit)
            profile.fullName = fullNameEdit.text.toString()
            profile.nickname = nicknameEdit.text.toString()
            profile.location = localityEdit.text.toString()
            profile.birthday = LocalDate.parse(
                birthdayEdit.text.toString(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
            Picasso.with(applicationContext).load(profile.imageUri).placeholder(R.drawable.person).into(imageEdit)

            if (image_uri != null) {
                try {
                    profile.imageUri = BitmapUtils.saveToInternalStorage(
                        applicationContext,
                        BitmapUtils.uriToBitmap(contentResolver, image_uri!!
                        )!!,profile.imageUri
                    )!!
                } catch (_: IOException) {

                }
            }

            val newProfileJSON = profile.toJSON().toString()
            putString("profile", newProfileJSON)
            intent.putExtra("newProfile", newProfileJSON)
            apply()
        }
    }

    private fun finishActivity() {
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Open sport selection dialog
     */
    private fun openSportSelectionDialog() {
        val dialog = EditSportsDialog()
        dialog.show(supportFragmentManager, "edit_sports")
    }

    fun showDatePickerDialog(v: View) {
        datePicker = DatePickerDialog(this, profile.birthday.year)
        datePicker.show()
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
                finishActivity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, selectedItems: ArrayList<Sports?>) {
        // ** Add new selected sports to user profile (and save to shared preferences)
        val newSports = mutableListOf<Sport>()
        val alreadySelectedSports = profile.sports
        for (selectedSport in selectedItems) {
            try {
                checkNotNull(selectedSport)
                val newSport: Sport = Sport(selectedSport, Skills.NEWBIE, 0)
                newSports.add(newSport)
            } catch (e: java.lang.IllegalStateException) {
                continue
            }
        }
        profile.sports = newSports.plus(alreadySelectedSports)
        println("Profile Sports After ADD: ${profile.sports}")
        saveEdit()

        profile.populateSportCardsEdit(this, sportContainer, onDeleteSport = { saveEdit() })
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        return
    }
}