package it.polito.mad.buddybench.utils

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.invitations.InvitationsFragment
import it.polito.mad.buddybench.activities.findcourt.FindCourtFragment
import it.polito.mad.buddybench.activities.friends.FriendsFragment
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.activities.myreservations.MyReservationsFragment
import it.polito.mad.buddybench.enums.Tabs
import nl.joery.animatedbottombar.AnimatedBottomBar

class BottomBar(val context: HomeActivity) {

    var currentTab = Tabs.RESERVATIONS
    lateinit var bottomBar: AnimatedBottomBar
    val counter: HashMap<Int, Int> = HashMap()
    fun setup() {
        bottomBar = context.findViewById(R.id.bottom_bar)
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {

                if (lastTab == newTab) return
                val newTag = Tabs.valueOf(newTab.title.replace(" ", "").uppercase())
                val lastTag = Tabs.valueOf(currentTab.name)
                currentTab = newTag
                replaceFragment(lastTag, newTag)
            }
        })
        addInitialFragment()
        initializeProfile()
    }

    private fun initializeProfile() {
        replaceFragment(currentTab, Tabs.PROFILE)
        context.supportFragmentManager.executePendingTransactions()
        replaceFragment(Tabs.PROFILE, Tabs.FRIENDS)
        context.supportFragmentManager.executePendingTransactions()
        replaceFragment(Tabs.FRIENDS, currentTab)
        context.supportFragmentManager.executePendingTransactions()

    }

    private fun addInitialFragment() {
        val transaction = context.supportFragmentManager.beginTransaction()
        val newFragment = when (currentTab) {
            Tabs.PROFILE -> ShowProfileFragment()
            Tabs.RESERVATIONS -> MyReservationsFragment(context)
            Tabs.FIND -> FindCourtFragment(context)
            Tabs.FRIENDS -> FriendsFragment(context)
            Tabs.INVITATIONS -> InvitationsFragment(context)
        }
        transaction.add(R.id.home_fragment_container, newFragment, currentTab.name)
        transaction.commitNow()
        val bottomBar = context.findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        bottomBar.selectTabAt(tabIndex = currentTab.getId())
        if (currentTab != Tabs.PROFILE)
            adjustExternalComponents()
    }

    fun replaceFragment(lastTag: Tabs, newTag: Tabs, update: Boolean = false) {
        val transaction = context.supportFragmentManager.beginTransaction()
        context.supportFragmentManager.findFragmentByTag(lastTag.name).let {
            if (it != null) {
                transaction.hide(it)
            }
        }
        context.supportFragmentManager.findFragmentByTag(newTag.name).let {
            if (it == null) {
                val newFragment = when (newTag) {
                    Tabs.PROFILE -> ShowProfileFragment()
                    Tabs.RESERVATIONS -> MyReservationsFragment(context)
                    Tabs.FIND -> FindCourtFragment(context)
                    Tabs.INVITATIONS -> InvitationsFragment(context)
                    Tabs.FRIENDS -> FriendsFragment(context)
                }

                transaction.add(R.id.home_fragment_container, newFragment, newTag.name)
            } else {
                transaction.show(it)
            }
        }
        transaction.commitNow()
        if(update)
            currentTab = newTag
        adjustExternalComponents()


    }

    private fun adjustExternalComponents() {
        if (currentTab == Tabs.INVITATIONS) {
            bottomBar.clearBadgeAtTabIndex(currentTab.getId())
        } else {
            if ((counter[Tabs.INVITATIONS.getId()] ?: 0) > 0)
                bottomBar.setBadgeAtTabIndex(
                    Tabs.INVITATIONS.getId(), AnimatedBottomBar.Badge(
                        text = counter[Tabs.INVITATIONS.getId()]!!.toString(),
                        textColor = Color.WHITE
                    )
                )
        }

        if (currentTab == Tabs.FRIENDS) {
            bottomBar.clearBadgeAtTabIndex(currentTab.getId())
        } else {
            if ((counter[Tabs.FRIENDS.getId()] ?: 0) > 0)
                bottomBar.setBadgeAtTabIndex(
                    Tabs.FRIENDS.getId(), AnimatedBottomBar.Badge(
                        text = counter[Tabs.FRIENDS.getId()]!!.toString(),
                        textColor = Color.WHITE
                    )
                )
        }

        if (currentTab == Tabs.RESERVATIONS) {
            bottomBar.clearBadgeAtTabIndex(currentTab.getId())
        } else {
            if ((counter[Tabs.RESERVATIONS.getId()] ?: 0) > 0)
                bottomBar.setBadgeAtTabIndex(
                    Tabs.RESERVATIONS.getId(), AnimatedBottomBar.Badge(
                        text = counter[Tabs.RESERVATIONS.getId()]!!.toString(),
                        textColor = Color.WHITE
                    )
                )
        }

//        when(this.currentTab){
//            Tabs.PROFILE -> {setToolbar() }
//            else -> {clearToolbar()}
//        }
    }

//    private fun setToolbar(){
//        context.findViewById<Toolbar?>(R.id.toolbar).let {
//            if(it != null){
//                it.visibility = View.VISIBLE
//                return
//            }
//        }
//        val header = context.findViewById<LinearLayout>(R.id.header)
//        val tb = context.layoutInflater.inflate(R.layout.toolbar, header)
//        val toolbar = context.findViewById<Toolbar>(R.id.toolbar)
//        toolbar.title = "Profile"
//        toolbar.setTitleTextColor(Color.WHITE)
//        context.setSupportActionBar(toolbar)
//    }
//
//    private fun clearToolbar(){
//        context.findViewById<Toolbar?>(R.id.toolbar)?.visibility = View.GONE
//    }

}