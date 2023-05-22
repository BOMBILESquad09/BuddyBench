package it.polito.mad.buddybench.activities.friends

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentContainerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.findcourt.FindCourtFragment
import it.polito.mad.buddybench.activities.myreservations.MyReservationsFragment
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.classes.Profile
import org.json.JSONObject

@AndroidEntryPoint
class FriendProfile: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.friend_profile)

        val fragmentContainer = findViewById<FragmentContainerView>(R.id.friend_fragment_container)
        val fragmentToAdd = ShowProfileFragment()
        println("HElloooooooooooooooooooo")


        val profile = Profile.mockProfile()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.friend_fragment_container, ShowProfileFragment())
            .commit()
    }



}