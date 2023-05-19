package it.polito.mad.buddybench.activities.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject

@AndroidEntryPoint
class ShowProfileActivity : AppCompatActivity() {

    private lateinit var profile: Profile
    private lateinit var sharedPref: SharedPreferences
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Profile"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
        userViewModel.user.observe(this) {
            profile = it
            setGUI()
        }
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
        reliabilityTv.text = getString(R.string.reliabilityValue).format(profile.reliability)

        val sportContainer = findViewById<LinearLayout>(R.id.sportsContainerEdit)
        sportContainer.removeAllViews()

        setProfileImage(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    private fun setProfileImage(uri: Uri?) {

        val iv = findViewById<ImageView>(R.id.profile_image)
        resizeImageView(iv)

        if (uri != null) {
            try{
                iv.setImageURI(uri)
            } catch (_: Exception){
                iv.setImageResource(R.drawable.person)
            }
            return
        }

        try{
            iv.setImageURI(profile.imageUri)
        } catch (_: Exception){
            iv.setImageResource(R.drawable.person)
        }
    }

    private  fun resizeImageView(iv: ImageView){
        val ll = findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height
            if (width == height) return@post
            val diameter = width.coerceAtMost(height)

            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }
    }
}