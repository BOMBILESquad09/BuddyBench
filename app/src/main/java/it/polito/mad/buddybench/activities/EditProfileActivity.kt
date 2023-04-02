package it.polito.mad.buddybench.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.squareup.picasso.Picasso
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.changeColor
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateEmail
import it.polito.mad.buddybench.classes.ValidationUtils.Companion.validateString
import it.polito.mad.buddybench.dialogs.EditSportsDialog
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditProfileActivity : AppCompatActivity(), EditSportsDialog.NoticeDialogListener {
    // ** Data
    private lateinit var profile: Profile
    private lateinit var datePicker: DatePickerDialog
    private var birthdateListener:MutableLiveData<LocalDate> = MutableLiveData()

    // ** Profile Image
    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onCameraImageReturned(it)
        }
    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onGalleryImageReturned(it)
        }

    private lateinit var imageEdit: ImageView
    private var imageUri: Uri? = null

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
            changeColor(fullNameEdit, validateString(text.toString()), resources)
        }
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(nicknameEdit, validateString(text.toString()), resources)

        }
        nicknameEdit.setText(profile.nickname)

        val emailEdit = findViewById<EditText>(R.id.emailEdit)
        emailEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(emailEdit, validateEmail(text.toString()), resources)
        }
        emailEdit.setText(profile.email)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)
        localityEdit.doOnTextChanged { text, _, _, _ ->
            changeColor(localityEdit, validateString(text.toString()), resources)

        }

        birthdateListener.value = profile.birthdate
        val birthdayButtonEdit = findViewById<Button>(R.id.birthdayEditButton)
        birthdateListener.observe(this){
            birthdayButtonEdit.text = it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            profile.birthdate = it
        }
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
        sportContainer = findViewById(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        profile.populateSportCardsEdit(this, sportContainer, onDeleteSport = { saveEdit() }, onSkillSelected = { saveEdit() })

        // ** Add Sports Button
        addSportButton = findViewById(R.id.add_sport_button)
        addSportButton.setOnClickListener { openSportSelectionDialog() }
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
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            launcherCamera.launch(cameraIntent)
        }
    }

    private fun onCameraImageReturned(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode != Activity.RESULT_OK) return
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, imageUri!!)
        imageEdit.setImageBitmap(bitmap)
    }

    private fun onGalleryImageReturned(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode != Activity.RESULT_OK) return
        imageUri = response.data?.data
        val bitmap = BitmapUtils.uriToBitmap(contentResolver, imageUri!!)
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

        if (!validateString(fullNameEdit.text.toString())) {
            return false
        }
        if (!validateString(nicknameEdit.text.toString())) {
            return false
        }
        if (!validateEmail(emailEdit.text.toString())) {
            return false
        }
        if (!validateString(localityEdit.text.toString())) {
            return false
        }
        if (birthdateListener.value == null) {
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
            val emailEdit = findViewById<EditText>(R.id.emailEdit)
            profile.fullName = fullNameEdit.text.toString()
            profile.nickname = nicknameEdit.text.toString()
            profile.location = localityEdit.text.toString()
            profile.birthdate = birthdateListener.value!!
            profile.email = emailEdit.text.toString()
            Picasso.with(applicationContext).load(profile.imageUri).placeholder(R.drawable.person).into(imageEdit)

            if (imageUri != null) {
                try {
                    profile.imageUri = BitmapUtils.saveToInternalStorage(
                        applicationContext,
                        BitmapUtils.uriToBitmap(contentResolver, imageUri!!
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
        val myDateListener = DatePickerDialog.OnDateSetListener { _, year, month, day -> birthdateListener.value = LocalDate.of(year,month + 1,day) }
        datePicker = DatePickerDialog(this, myDateListener, profile.birthdate.year, profile.birthdate.monthValue, profile.birthdate.dayOfMonth)
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