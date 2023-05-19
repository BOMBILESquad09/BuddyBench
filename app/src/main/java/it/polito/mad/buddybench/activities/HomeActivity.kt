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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.profile.EditProfileActivity
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.utils.BottomBar
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {

    private val bottomBar = BottomBar(this)
    private val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onEditReturn(it)}
    val launcherReservation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ onReservationReturn(it)}
    val launcherReviews = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { onReviewsReturn(it) }
    val invitationsViewModel by viewModels<InvitationsViewModel>()

    lateinit var profile: Profile
    private lateinit var sharedPref: SharedPreferences
    val userViewModel by viewModels<UserViewModel>()
    val findCourtViewModel by viewModels<FindCourtViewModel>()
    val reservationViewModel by viewModels<ReservationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        reservationViewModel.email = Firebase.auth.currentUser!!.email!!
        invitationsViewModel.subscribeInvitations()


        userViewModel.getUser(Firebase.auth.currentUser!!.email!!).observe(this){
            if (it==null){
                profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
            }
            if(it != null)
                profile = it
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
                if(!::profile.isInitialized)
                    profile = Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
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
                val newImageUri =  if(newProfile.imageUri != null &&  newProfile.imageUri.toString() != profile.imageUri.toString() )
                    BitmapUtils.saveToInternalStorage(applicationContext, BitmapUtils.uriToBitmap(contentResolver, newProfile.imageUri!!)!!, profile.imageUri) else profile.imageUri
                if(newImageUri == null){
                    val toast = Toast.makeText(
                        applicationContext,
                        "Something went wrong while saving the profile image...",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
                val oldEmail = profile.email
                profile = newProfile
                profile.imageUri = newImageUri?: profile.imageUri
                putString("profile", profile.toJSON().toString())
                apply()
                userViewModel.setSports(profile.sports)
                userViewModel.updateUserInfo(profile, oldEmail)
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

    private fun onReservationReturn(response: androidx.activity.result.ActivityResult){
        if(response.resultCode  == Activity.RESULT_OK){
            reservationViewModel.refresh = true

            reservationViewModel.updateSelectedDay( LocalDate.parse(response.data!!.getStringExtra("date"), DateTimeFormatter.ISO_LOCAL_DATE))
            bottomBar.replaceFragment(bottomBar.currentTab, Tabs.RESERVATIONS)
            bottomBar.currentTab = Tabs.RESERVATIONS
            bottomBar.bottomBar.selectTabAt(tabIndex = bottomBar.currentTab.getId())
        }
    }

    private fun onReviewsReturn(response: ActivityResult) {
        if (response.resultCode  == Activity.RESULT_OK){
           // TODO: Maybe update
           println("Returned from reviews activity")
        }
    }
}