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
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
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
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        profile = Profile.fromJSON(JSONObject(intent.getStringExtra("profile")!!))
        val fullNameEdit = findViewById<EditText>(R.id.fullNameEdit)
        fullNameEdit.setText(profile.fullName)

        val nicknameEdit = findViewById<EditText>(R.id.nicknameEdit)
        nicknameEdit.setText(profile.nickname)

        val localityEdit = findViewById<EditText>(R.id.localityEdit)
        localityEdit.setText(profile.location)

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
        for(sport in profile.sports){
            val sportCard = LayoutInflater.from(this).inflate(R.layout.card_sport, null, false);

            // ** Sport card dynamic values
            val sportName = sportCard.findViewById<TextView>(R.id.sport_card_name);
            val sportIcon = sportCard.findViewById<ImageView>(R.id.sport_card_icon);
            val sportSkillLevel = sportCard.findViewById<CardView>(R.id.skill_level_card)
            val sportSkillLevelText = sportCard.findViewById<TextView>(R.id.skill_level_card_text)
            val sportGamesPlayed = sportCard.findViewById<TextView>(R.id.games_played_text)

            sportName.text = Utils.capitalize(sport.name.toString())
            sportIcon.setImageResource(Sports.sportToIconDrawable(sport.name))
            // TODO: Non funziona
            // sportSkillLevel.setBackgroundColor(Skills.skillToColor(sport.skill))
            sportSkillLevelText.text = Utils.capitalize(sport.skill.toString())
            sportGamesPlayed.text = String.format(resources.getString(R.string.games_played), sport.matchesPlayed)

            // ** Add card to container
            sportContainer.addView(sportCard)
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
    fun checkCameraPermission(): Boolean{

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            putString("profile", profile.toJSON().toString())
            intent.putExtra("newProfile", profile.toJSON().toString())
            println("new profile is")
            println(profile.toJSON().toString())
            apply()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
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