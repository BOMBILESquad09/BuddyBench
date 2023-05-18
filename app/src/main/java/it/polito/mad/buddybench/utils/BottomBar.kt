package it.polito.mad.buddybench.utils

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.findcourt.FindCourtFragment
import it.polito.mad.buddybench.activities.friends.FriendsFragment
import it.polito.mad.buddybench.activities.profile.ShowProfileFragment
import it.polito.mad.buddybench.activities.myreservations.MyReservationsFragment
import it.polito.mad.buddybench.enums.Tabs
import nl.joery.animatedbottombar.AnimatedBottomBar

class BottomBar(val context: HomeActivity) {

    var currentTab = Tabs.RESERVATIONS
    lateinit var bottomBar: AnimatedBottomBar
    fun setup(){
        bottomBar = context.findViewById(R.id.bottom_bar)
        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {

                if(lastTab == newTab) return
                val newTag = Tabs.valueOf( newTab.title.replace(" ","").uppercase())
                val lastTag = Tabs.valueOf(currentTab.name)
                currentTab = newTag
                replaceFragment(lastTag,newTag)
            }
        })
        addInitialFragment()
        initializeProfile()
    }

    private fun initializeProfile(){
        replaceFragment(currentTab, Tabs.PROFILE)
        context.supportFragmentManager.executePendingTransactions()
        replaceFragment(Tabs.PROFILE, currentTab)
    }

    private fun addInitialFragment(){
        val transaction = context.supportFragmentManager.beginTransaction()
        val newFragment =  when(currentTab){
            Tabs.PROFILE -> ShowProfileFragment(context)
            Tabs.RESERVATIONS -> MyReservationsFragment(context)
            Tabs.FINDCOURT -> FindCourtFragment(context)
            Tabs.FRIENDS -> FriendsFragment(context)
        }
        transaction.add(R.id.home_fragment_container, newFragment, currentTab.name)
        transaction.commitNow()
        val bottomBar = context.findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        bottomBar.selectTabAt(tabIndex = currentTab.getId())
        if (currentTab != Tabs.PROFILE)
            adjustExternalComponents()
    }

    fun replaceFragment(lastTag: Tabs, newTag: Tabs){
        val transaction = context.supportFragmentManager.beginTransaction()

        context.supportFragmentManager.findFragmentByTag(lastTag.name).let {
            println(it)
            println("watch--------------------------")
            if(it != null){
                transaction.hide(it)
            }
        }
        context.supportFragmentManager.findFragmentByTag(newTag.name).let {
            if (it == null){
                val newFragment =  when(newTag){
                    Tabs.PROFILE -> ShowProfileFragment(context)
                    Tabs.RESERVATIONS -> MyReservationsFragment(context)
                    Tabs.FINDCOURT -> FindCourtFragment(context)
                    Tabs.FRIENDS -> FriendsFragment(context)
                }

                transaction.add(R.id.home_fragment_container, newFragment, newTag.name)
            } else {
                transaction.show(it)
            }
        }
        transaction.commit()
        adjustExternalComponents()

    }

    private fun adjustExternalComponents(){

        when(this.currentTab){
            Tabs.PROFILE -> {setToolbar() }
            else -> {clearToolbar()}
        }
    }

    private fun setToolbar(){
        context.findViewById<Toolbar?>(R.id.toolbar).let {
            if(it != null){
                it.visibility = View.VISIBLE
                return
            }
        }
        val header = context.findViewById<LinearLayout>(R.id.header)
        val tb = context.layoutInflater.inflate(R.layout.toolbar, header)
        val toolbar = context.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Profile"
        toolbar.setTitleTextColor(Color.WHITE)
        context.setSupportActionBar(toolbar)
    }

    private fun clearToolbar(){
        context.findViewById<Toolbar?>(R.id.toolbar)?.visibility = View.GONE
    }

}