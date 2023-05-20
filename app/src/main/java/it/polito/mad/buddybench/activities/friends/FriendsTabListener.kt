package it.polito.mad.buddybench.activities.friends

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class FriendsTabListener(private val viewPager: ViewPager2): OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            this.viewPager.currentItem = tab.position
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        return
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        return
    }
}