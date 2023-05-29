package it.polito.mad.buddybench.activities

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
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
import androidx.compose.ui.text.capitalize
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.profile.EditProfileActivity
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.classes.BitmapUtils
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.BottomBar
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel
import it.polito.mad.buddybench.viewmodels.FriendsViewModel
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val bottomBar = BottomBar(this)
    val launcherEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onEditReturn(it)
    }
    val launcherReservation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onReservationReturn(it)
        }
    val launcherReviews =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onReviewsReturn(it)
        }
    val launcherActivityFriendProfile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onFriendsProfileReturn(it)
        }

    val invitationsViewModel by viewModels<InvitationsViewModel>()

    private lateinit var sharedPref: SharedPreferences
    val imageViewModel by viewModels<ImageViewModel>()
    val userViewModel by viewModels<UserViewModel>()
    val findCourtViewModel by viewModels<FindCourtViewModel>()
    val reservationViewModel by viewModels<ReservationViewModel>()
    val friendsViewModel by viewModels<FriendsViewModel>()
    var initInvitations = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        onNetworkProblemHandler()
        createNotificationChannels()

        setContentView(R.layout.home)
        sharedPref =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        reservationViewModel.email = Firebase.auth.currentUser!!.email!!
        bottomBar.setup()

        invitationsViewModel.popNotification  = { it -> createInvitationNotification(it)}
        friendsViewModel.popNotification = {it -> createFriendRequestNotification(it)}
        userViewModel.getUser(Firebase.auth.currentUser!!.email!!).observe(this) {
            if (it != null){

                friendsViewModel.subscribeFriendsList()

                invitationsViewModel.subscribeInvitations() { s ->
                    if (s > 0) {
                        bottomBar.counter[Tabs.INVITATIONS.getId()] = s
                        if (bottomBar.currentTab != Tabs.INVITATIONS)
                            bottomBar.bottomBar.setBadgeAtTabIndex(
                                Tabs.INVITATIONS.getId(), AnimatedBottomBar.Badge(
                                    text = s.toString(),
                                    textColor = Color.WHITE
                                )
                            )

                        initInvitations = true
                    } else {
                        bottomBar.counter[Tabs.INVITATIONS.getId()] = 0
                        bottomBar.bottomBar.clearBadgeAtTabIndex(Tabs.INVITATIONS.getId())

                    }
                    friendsViewModel.friendRequests.observe(this) {
                        if (it.isNotEmpty()) {
                            bottomBar.counter[Tabs.FRIENDS.getId()] = it.size
                            if (bottomBar.currentTab != Tabs.FRIENDS)
                                bottomBar.bottomBar.setBadgeAtTabIndex(
                                    Tabs.FRIENDS.getId(),
                                    AnimatedBottomBar.Badge(
                                        text = it.size.toString(),
                                        textColor = Color.WHITE
                                    )
                                )
                        } else {
                            bottomBar.counter[Tabs.FRIENDS.getId()] = 0
                            bottomBar.bottomBar.clearBadgeAtTabIndex(Tabs.FRIENDS.getId())
                        }
                    }
                }
                return@observe
            }
            userViewModel.fromSharedPreferences(
                Profile.fromJSON(
                    JSONObject(
                        sharedPref.getString(
                            "profile",
                            Profile.mockJSON()
                        )!!
                    )
                )
            )
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
                if (userViewModel.user.value == null) return false
                intent.putExtra("profile", userViewModel.user.value!!.toJSON().toString())
                launcherEdit.launch(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onEditReturn(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            with(sharedPref.edit()) {
                val newProfile = Profile.fromJSON(
                    JSONObject(
                        response.data?.getStringExtra("newProfile").toString()
                    )
                )
                putString("profile", newProfile.toJSON().toString())
                apply()
                userViewModel.setSports(newProfile.sports)
                if (newProfile.imageUri != null && response.data?.getBooleanExtra(
                        "newImage",
                        false
                    ) == true
                ) {

                    imageViewModel.postUserImage(newProfile.email, newProfile.imageUri!!, {
                        userViewModel.getUser()
                    }) {}
                }
                userViewModel.updateUserInfo(newProfile, {}) {}


            }
        }
    }

    private fun onReservationReturn(response: androidx.activity.result.ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            reservationViewModel.refresh = true
            reservationViewModel.updateSelectedDay(
                LocalDate.parse(
                    response.data!!.getStringExtra("date"),
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
            )
            reservationViewModel.getAllByUser()

            bottomBar.replaceFragment(bottomBar.currentTab, Tabs.RESERVATIONS)
            bottomBar.currentTab = Tabs.RESERVATIONS
            bottomBar.bottomBar.selectTabAt(tabIndex = bottomBar.currentTab.getId())
        }
    }

    private fun onFriendsProfileReturn(response: ActivityResult?) {
        friendsViewModel.refreshAll {  }
    }

    private fun onReviewsReturn(response: ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            // TODO: Maybe update
        }
    }

    override fun onStart() {
        super.onStart()
        Utils.closeProgressDialog()
    }



    private fun onNetworkProblemHandler() {


        imageViewModel.onFailure = {
            Utils.openNetworkProblemDialog(this)
        }

        reservationViewModel.onFailure = {
            Utils.openNetworkProblemDialog(this)
        }

        userViewModel.onFailure = {
            Utils.openNetworkProblemDialog(this)
        }

        findCourtViewModel.onFailure = {
            Utils.openNetworkProblemDialog(this)
        }
        friendsViewModel.onFailure = {

            Utils.openNetworkProblemDialog(this)
        }

    }

    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.friend_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("friendRequests", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.invitation_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("invitationRequests", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.invitation_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("joinRequests", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun createInvitationNotification(reservationDTO: ReservationDTO){

        val intent = Intent(this, HomeActivity::class.java).apply {
        }
        intent.putExtra("tab", Tabs.INVITATIONS.name)

        val text = "%s invited you to play %s!".format(reservationDTO.userOrganizer.nickname,
            reservationDTO.court.sport.lowercase().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ENGLISH
                ) else it.toString()
            })

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            Tabs.INVITATIONS.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, "invitationRequests")
            .setSmallIcon(R.drawable.tennis)
            .setContentTitle(getString(R.string.invitation_channel_description))
            .setContentText(text)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(reservationDTO.id.hashCode(), builder.build())
            }

        }catch (_: SecurityException){
        }
    }

    private fun createFriendRequestNotification(profile: Profile){
        val intent = Intent(this, HomeActivity::class.java).apply {
        }
        intent.putExtra("tab", Tabs.FRIENDS.name)
        val text = "%s sent you a friend request!".format(profile.fullName)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            Tabs.FRIENDS.getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, "friendRequests")
            .setSmallIcon(R.drawable.add_friend)
            .setContentTitle(getString(R.string.friend_channel_description))
            .setContentText(text)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(profile.email.hashCode(), builder.build())
            }

        }catch (_: SecurityException){
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.getStringExtra("tab") == null) return

        val tab = intent.getStringExtra("tab")!!
        bottomBar.replaceFragment(bottomBar.currentTab, Tabs.valueOf(tab), true)
    }

}