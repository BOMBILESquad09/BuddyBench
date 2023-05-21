package it.polito.mad.buddybench.activities.friends

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FriendsViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FriendListFragment()
            1 -> FriendRequestFragment()
            2 -> FindFriendFragment()
            else -> FindFriendFragment()
        }
    }
}