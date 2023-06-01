package it.polito.mad.buddybench.activities.findcourt

import com.google.android.material.tabs.TabLayout

class FindTabListener(private val callback: (tabIndex: Int) -> Unit): TabLayout.OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
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