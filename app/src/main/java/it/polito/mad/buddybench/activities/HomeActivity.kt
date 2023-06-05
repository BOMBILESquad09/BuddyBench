package it.polito.mad.buddybench.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.FriendsFragment
import it.polito.mad.buddybench.activities.profile.EditProfileActivity
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.BottomBar
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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

    private val invitationsViewModel by viewModels<InvitationsViewModel>()

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

        reservationViewModel.email = Firebase.auth.currentUser!!.email!!
        bottomBar.setup()
        sharedPref =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        userViewModel.sharedPref = sharedPref
        invitationsViewModel.popNotification = { it -> createInvitationNotification(it) }
        friendsViewModel.popNotification = { it -> createFriendRequestNotification(it) }
        reservationViewModel.popNotification = {r,p -> createJoinRequestNotification(r,p)}
        userViewModel.getUser(Firebase.auth.currentUser!!.email!!).observe(this) {
            if (it != null && it.email != "") {
                friendsViewModel.subscribeFriendsList()
                invitationsViewModel.subscribeInvitations { s ->
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

                }
                friendsViewModel.friendRequests.observe(this) { p->
                    if (p.isNotEmpty()) {
                        bottomBar.counter[Tabs.FRIENDS.getId()] = p.size
                        if (bottomBar.currentTab != Tabs.FRIENDS)
                            bottomBar.bottomBar.setBadgeAtTabIndex(
                                Tabs.FRIENDS.getId(),
                                AnimatedBottomBar.Badge(
                                    text = p.size.toString(),
                                    textColor = Color.WHITE
                                )
                            )
                    } else {
                        bottomBar.counter[Tabs.FRIENDS.getId()] = 0
                        bottomBar.bottomBar.clearBadgeAtTabIndex(Tabs.FRIENDS.getId())
                    }
                }

                reservationViewModel.subscribeReservations { s ->
                    println("...............................")
                    if (s > 0) {
                        bottomBar.counter[Tabs.RESERVATIONS.getId()] = s
                        if (bottomBar.currentTab != Tabs.RESERVATIONS)
                            bottomBar.bottomBar.setBadgeAtTabIndex(
                                Tabs.RESERVATIONS.getId(), AnimatedBottomBar.Badge(
                                    text = s.toString(),
                                    textColor = Color.WHITE
                                )
                            )

                    } else {
                        bottomBar.counter[Tabs.RESERVATIONS.getId()] = 0
                        bottomBar.bottomBar.clearBadgeAtTabIndex(Tabs.RESERVATIONS.getId())

                    }
                }
                println("-------------------------------------------------")
                return@observe
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

                Utils.openProgressDialog(this@HomeActivity)

                userViewModel.updateUserInfo(newProfile, onFailure = {
                    Utils.closeProgressDialog()
                    Utils.openGeneralProblemDialog(
                        "Error",
                        "An error occurred while updating your profile, try later",
                        this@HomeActivity
                    )
                }) {
                    if (newProfile.imageUri != null && response.data?.getBooleanExtra(
                            "newImage",
                            false
                        ) == true
                    ) {
                        imageViewModel.postUserImage(newProfile.email, newProfile.imageUri!!, {
                            Utils.closeProgressDialog()

                            Utils.openGeneralProblemDialog(
                                "Error",
                                "An error occurred while updating the image, try later",
                                this@HomeActivity
                            )
                        }) {
                            Utils.closeProgressDialog()
                            userViewModel.getUser()
                        }
                    } else{
                        Utils.closeProgressDialog()
                    }

                }
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
        friendsViewModel.refreshAll { }
    }

    private fun onReviewsReturn(response: ActivityResult) {
        if (response.resultCode == Activity.RESULT_OK) {
            // TODO: Maybe update
        }
    }

    override fun onStart() {
        super.onStart()
        onNewIntent(intent)
        findViewById<View>(R.id.progress_bar).visibility = View.GONE

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

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.friend_requests_channel_name)
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
            val name = getString(R.string.invitation_channel_description)
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

            val name = getString(R.string.join_requests_channel_name)
            val descriptionText = getString(R.string.join_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("joinRequests", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


    }


    private fun createInvitationNotification(reservationDTO: ReservationDTO) {

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
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "invitationRequests")
            .setSmallIcon(R.drawable.ic_notification_large)
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

        } catch (_: SecurityException) {
        }
    }

    private fun createFriendRequestNotification(profile: Profile) {
        val intent = Intent(this, HomeActivity::class.java).apply {
        }
        intent.putExtra("tab", Tabs.FRIENDS.name)
        val text = "%s sent you a friend request!".format(profile.fullName)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            Tabs.FRIENDS.getId(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "friendRequests")
            .setSmallIcon(R.drawable.ic_notification_large)

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

        } catch (_: SecurityException) {
        }
    }


    private fun createJoinRequestNotification(reservationDTO: ReservationDTO, profile: Profile) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("date", reservationDTO.date.toString())
        intent.putExtra("tab", Tabs.RESERVATIONS.name)


        val text = "%s wants to join in your match at %s!".format(profile.fullName, reservationDTO.court.name)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            reservationDTO.date.toEpochDay().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "joinRequests")
            .setSmallIcon(R.drawable.ic_notification_large)

            .setContentTitle(getString(R.string.join_channel_description))
            .setContentText(text)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)

            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(this)) {
                notify(reservationDTO.hashCode(), builder.build())
            }

        } catch (_: SecurityException) {
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getStringExtra("tab") == null) return

        val tab = intent.getStringExtra("tab")!!
        bottomBar.replaceFragment(bottomBar.currentTab, Tabs.valueOf(tab), true)
        bottomBar.bottomBar.selectTabAt(Tabs.valueOf(tab).getId())
        supportFragmentManager.executePendingTransactions()
        if (tab == Tabs.FRIENDS.name) {
            val fragment: FriendsFragment =
                supportFragmentManager.findFragmentByTag(Tabs.FRIENDS.name) as FriendsFragment
            fragment.binding.tabFriends.selectTab(fragment.binding.tabFriends.getTabAt(1))
            fragment.binding.tabFriendsViewpager.setCurrentItem(1, true)
        }
        if(tab == Tabs.RESERVATIONS.name){
            val date = LocalDate.parse(intent.getStringExtra("date"), DateTimeFormatter.ISO_LOCAL_DATE)
            reservationViewModel.updateSelectedDay(
                date
            )
        }

    }

}