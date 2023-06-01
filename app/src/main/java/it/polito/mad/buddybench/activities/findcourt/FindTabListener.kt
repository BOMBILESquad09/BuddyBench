package it.polito.mad.buddybench.activities.findcourt

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

public val COURTS_TAB = 0;
public val GAMES_TAB = 1;

class FindTabListener(private val viewPager: ViewPager2, private val callback: (tabIndex: Int) -> Unit): TabLayout.OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            this.viewPager.currentItem = tab.position
            this.callback(tab.position)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        return
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        return
    }
}