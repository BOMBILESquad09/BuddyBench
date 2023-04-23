package it.polito.mad.buddybench.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.calendar.MyReservationsFragment
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.utils.BottomBar
import org.json.JSONObject

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {
    private val bottomBar = BottomBar(this)
    private val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onEditReturn(it)}
    lateinit var profile: Profile
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        profile = if (::profile.isInitialized) {
             profile
        } else{
            Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
        }
        bottomBar.setup()
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
            with(sharedPref.edit()) {
                val newProfile = Profile.fromJSON(JSONObject(response.data?.getStringExtra("newProfile").toString()))
                val newImageUri =  if(newProfile.imageUri != null &&  newProfile.imageUri.toString() != profile.imageUri.toString())
                    BitmapUtils.saveToInternalStorage(applicationContext, BitmapUtils.uriToBitmap(contentResolver, newProfile.imageUri!!)!!, profile.imageUri) else profile.imageUri
                if(newImageUri == null){
                    val toast = Toast.makeText(
                        applicationContext,
                        "Something went wrong while saving the profile image...",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
                profile = newProfile
                profile.imageUri = newImageUri?: profile.imageUri

                putString("profile", profile.toJSON().toString())
                apply()
                supportFragmentManager.findFragmentByTag(Tabs.PROFILE.name).let {
                    if (it != null){
                        (it as ShowProfileFragment).let {
                            it.profile = profile
                        }
                    }
                }
            }
        }
    }
}