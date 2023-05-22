package it.polito.mad.buddybench.activities.friends

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.polito.mad.buddybench.activities.HomeActivity

class FriendsViewPagerAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {


        return when (position) {
            0 -> FriendListFragment(fragment.requireActivity() as HomeActivity)
            1 -> FriendRequestFragment()
            2 -> FindFriendFragment()
            else -> FindFriendFragment()
        }
    }
}