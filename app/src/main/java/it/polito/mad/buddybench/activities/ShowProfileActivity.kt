package it.polito.mad.buddybench.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject
import org.w3c.dom.Text

class ShowProfileActivity : AppCompatActivity() {
    lateinit var profile:Profile
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))

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

        val sportContainer = findViewById<LinearLayout>(R.id.sportsContainerView)

        for(sport in profile.sports){
            val sportCard = LayoutInflater.from(this).inflate(R.layout.card_sport, null, false);
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
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}