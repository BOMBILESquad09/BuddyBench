package it.polito.mad.buddybench.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject


class ShowProfileActivity : AppCompatActivity() {
    private lateinit var profile: Profile
    private val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onEditReturn(it)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
        setGUI()
    }

    private fun setGUI(){
        val fullNameTv = findViewById<TextView>(R.id.fullNameView)
        fullNameTv.text = profile.fullName

        val nicknameTv = findViewById<TextView>(R.id.nickNameView)
        nicknameTv.text = profile.nickname

        val ageTv = findViewById<TextView>(R.id.ageView)
        ageTv.text = getString(R.string.age).format(profile.age)

        val emailTv = findViewById<TextView>(R.id.emailView)
        emailTv.text = profile.email

        val locationTv = findViewById<TextView>(R.id.locationView)
        locationTv.text = profile.location

        val matchesPlayedTv = findViewById<TextView>(R.id.matchesPlayedView)
        matchesPlayedTv.text = profile.matchesPlayed.toString()

        val matchesOrganizedTv = findViewById<TextView>(R.id.matchesOrganizedView)
        matchesOrganizedTv.text = profile.matchesOrganized.toString()

        val reliabilityTv = findViewById<TextView>(R.id.reliabilityView)
        reliabilityTv.text = profile.reliability.toString()

        val iv = findViewById<ImageView>(R.id.imageEdit)
        println(profile.imageUri)
        Picasso.with(applicationContext).load("file://${profile.imageUri}").placeholder(R.drawable.person).into(iv)


        val sportContainer = findViewById<LinearLayout>(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        // ** Populate sport cards
        profile.populateSportCards(this, sportContainer)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
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
            setGUI()
        }
    }
}