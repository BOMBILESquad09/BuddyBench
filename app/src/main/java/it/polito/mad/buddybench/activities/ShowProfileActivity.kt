package it.polito.mad.buddybench.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject


class ShowProfileActivity : AppCompatActivity() {
    private lateinit var profile: Profile
    private val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onEditReturn(it)}
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Profile"
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

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

        /*
        val emailTv = findViewById<TextView>(R.id.emailView)
        emailTv.text = profile.email*/

        val locationTv = findViewById<TextView>(R.id.locationView)
        locationTv.text = profile.location

        val matchesPlayedTv = findViewById<TextView>(R.id.matchesPlayedView)
        matchesPlayedTv.text = profile.matchesPlayed.toString()

        val matchesOrganizedTv = findViewById<TextView>(R.id.matchesOrganizedView)
        matchesOrganizedTv.text = profile.matchesOrganized.toString()

        val reliabilityTv = findViewById<TextView>(R.id.reliabilityView)
        reliabilityTv.text = getString(R.string.reliabilityValue).format(profile.reliability)

        val iv = findViewById<ImageView>(R.id.imageEdit)
        resizeImageView(iv)
        Picasso.with(applicationContext).load("${profile.imageUri}").placeholder(R.drawable.person).into(iv)


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
        println("hiii")
        if(response.resultCode == Activity.RESULT_OK){
            with(sharedPref.edit()) {

                val newProfile = Profile.fromJSON(JSONObject(response.data?.getStringExtra("newProfile").toString()))
                val newProfileJSON = newProfile.toJSON().toString()

                val newImageUri =  if(newProfile.imageUri != null &&  newProfile.imageUri.toString() != profile.imageUri.toString())
                    BitmapUtils.saveToInternalStorage(applicationContext, BitmapUtils.uriToBitmap(contentResolver, newProfile.imageUri!!)!!) else profile.imageUri
                profile.imageUri = newImageUri?:profile.imageUri
                println("salvatooo")

                if(newImageUri == null){
                    val toast = Toast.makeText(
                        applicationContext,
                        "Something went wrong while saving the profile image...",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
                profile = newProfile
                putString("profile", newProfileJSON)
                apply()

                setGUI()
            }
        }
    }

    private  fun resizeImageView(iv: ImageView){

        val ll = findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height
            val diameter = width.coerceAtMost(height)
            println(diameter)

            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }

    }


}