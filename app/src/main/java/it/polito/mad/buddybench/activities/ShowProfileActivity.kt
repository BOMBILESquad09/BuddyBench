package it.polito.mad.buddybench.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import org.json.JSONObject
import java.io.File
import java.io.FileDescriptor
import java.io.IOException

class ShowProfileActivity : AppCompatActivity() {
    lateinit var profile:Profile
    private val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onEditReturn(it)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(Color.parseColor("#80000000"));
        setSupportActionBar(toolbar)
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
        setGUI()
        checkCameraPermission()

    }

    private fun setGUI(){
        val fullNameTv = findViewById<TextView>(R.id.fullNameView)
        fullNameTv.text = profile.fullName

        val nicknameTv = findViewById<TextView>(R.id.nickNameView)
        nicknameTv.text = profile.nickname

        val ageTv = findViewById<TextView>(R.id.ageView)
        ageTv.text = getString(R.string.age).format(profile.age)

        val locationTv = findViewById<TextView>(R.id.locationView)
        locationTv.text = profile.location

        val matchesPlayedTv = findViewById<TextView>(R.id.matchesPlayedView)
        matchesPlayedTv.text = profile.matchesPlayed.toString()

        val matchesOrganizedTv = findViewById<TextView>(R.id.matchesOrganizedView)
        matchesOrganizedTv.text = profile.matchesOrganized.toString()

        val reliabilityTv = findViewById<TextView>(R.id.reliabilityView)
        reliabilityTv.text = profile.reliability.toString()

        val iv = findViewById<ImageView>(R.id.imageView)
        if (profile.imageUri != null ){
            try{
                iv.setImageURI(profile.imageUri)
            } catch (_: java.lang.Exception){
                /*maybe the image has been deleted*/
            }
        }


        /*if (profile.imageUri.toString() != null){
            getBitmapFromUri(profile.imageUri!!)

        }*/
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
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("profile", profile.toJSON().toString())
                launcherEdit.launch(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onEditReturn(response: androidx.activity.result.ActivityResult){
        if(response.resultCode == Activity.RESULT_OK){
            profile = Profile.fromJSON(JSONObject(response.data?.getStringExtra("newProfile").toString()))
            println("profile passed")
            println(profile.toJSON().toString())
            setGUI()
        }
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





}