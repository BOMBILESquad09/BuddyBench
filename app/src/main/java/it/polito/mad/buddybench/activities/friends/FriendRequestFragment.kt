package it.polito.mad.buddybench.activities.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * A fragment representing a list of Items.
 */
class FriendRequestFragment : Fragment(R.layout.fragment_friend_request_list) {

    // ** UI
    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var pbFriendRequests: ProgressBar

    // ** View Model
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    // ** Column count
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFriendRequests = view.findViewById(R.id.rv_friend_requests)
        pbFriendRequests = view.findViewById(R.id.pb_friend_requests)

        // ** Loading state
        friendsViewModel.lRequests.observe(viewLifecycleOwner) {
            if (it) {
                pbFriendRequests.visibility = View.VISIBLE
                rvFriendRequests.visibility = View.GONE
            } else {
                pbFriendRequests.visibility = View.GONE
                rvFriendRequests.visibility = View.VISIBLE
            }
        }

        with(rvFriendRequests) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FriendRequestRecyclerViewAdapter(listOf(), friendsViewModel)
        }
        // ** Data
        friendsViewModel.friendRequests.observe(viewLifecycleOwner) {
            if (it != null) {

                with(rvFriendRequests){
                    val oldList = friendsViewModel.oldFriendsRequests
                    val friendDiff = FriendListDiffUtils(oldList, it)
                    val diffs = DiffUtil.calculateDiff(friendDiff)

                    (adapter as FriendRequestRecyclerViewAdapter).values = it
                    diffs.dispatchUpdatesTo(adapter!!)
                }
            }
        }
    }
}