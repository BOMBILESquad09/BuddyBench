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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
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
import it.polito.mad.buddybench.viewmodels.FriendsViewModel
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import nl.joery.animatedbottombar.AnimatedBottomBar
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
    val imageViewModel by viewModels<ImageViewModel> ()
    val userViewModel by viewModels<UserViewModel>()
    val findCourtViewModel by viewModels<FindCourtViewModel>()
    val reservationViewModel by viewModels<ReservationViewModel>()
    val friendsViewModel by viewModels<FriendsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.home)
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        reservationViewModel.email = Firebase.auth.currentUser!!.email!!
        bottomBar.setup()

        userViewModel.getUser(Firebase.auth.currentUser!!.email!!).observe(this){
            profile = it ?: Profile.fromJSON(JSONObject( sharedPref.getString("profile", Profile.mockJSON())!!))
            friendsViewModel.subscribeFriendsList()
            invitationsViewModel.subscribeInvitations(){ s ->
                if(s > 0){
                    bottomBar.counter[Tabs.INVITATIONS.getId()] = s
                    bottomBar.bottomBar.setBadgeAtTabIndex(Tabs.INVITATIONS.getId(), AnimatedBottomBar.Badge(s.toString()))
                } else{
                    bottomBar.counter[Tabs.INVITATIONS.getId()] = 0
                }
            }

        }


        friendsViewModel.friendRequests.observe(this){
            if(it.isNotEmpty()){
                bottomBar.counter[Tabs.FRIENDS.getId()] = it.size
                bottomBar.bottomBar.setBadgeAtTabIndex(Tabs.FRIENDS.getId(), AnimatedBottomBar.Badge(it.size.toString()))
            } else{
                bottomBar.counter[Tabs.FRIENDS.getId()] = 0
                bottomBar.bottomBar.clearBadgeAtTabIndex(Tabs.FRIENDS.getId())

            }
        }






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

                profile = newProfile
                putString("profile", profile.toJSON().toString())
                apply()
                userViewModel.setSports(profile.sports)
                if(profile.imageUri != null && response.data?.getBooleanExtra("newImage", false) == true)
                    imageViewModel.postUserImage(profile.email, profile.imageUri!!)
                userViewModel.updateUserInfo(profile)

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
        }


    }



}