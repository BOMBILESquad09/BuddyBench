package it.polito.mad.buddybench.activities.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.databinding.FragmentFriendsBinding


/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment(val context: HomeActivity) : Fragment() {

    // ** NB: Autogenerated binding class containing all the elements of the .xml file
    // with an id. Example: binding.court_name_tv
    private var _binding: FragmentFriendsBinding? = null

    // ** This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val viewPagerAdapter = FriendsViewPagerAdapter(this)
        binding.tabFriendsViewpager.adapter = viewPagerAdapter
        binding.tabFriends.addOnTabSelectedListener(FriendsTabListener(viewPager = binding.tabFriendsViewpager))
        binding.tabFriendsViewpager.registerOnPageChangeCallback(FriendsOnPageChangeCallback(binding.tabFriends))

        // **  TODO: Check for requests and add the badge
        val requestsBadge: BadgeDrawable? = binding.tabFriends.getTabAt(1)?.orCreateBadge
        if (requestsBadge != null) {
            requestsBadge.number = 1
            requestsBadge.isVisible = true
        }
    }
}