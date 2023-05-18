package it.polito.mad.buddybench.activities.friends

import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout

class FriendsOnPageChangeCallback(private val tabLayout: TabLayout): OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        this.tabLayout.getTabAt(position)?.select()
        super.onPageSelected(position)
    }
}