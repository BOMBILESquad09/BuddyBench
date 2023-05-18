package it.polito.mad.buddybench.activities.friends

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendsViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        println("Position: $position")
        return when (position) {
            0 -> FriendRequestsFragment()
            1 -> FriendListFragment()
            else -> FriendRequestsFragment()
        }
    }
}