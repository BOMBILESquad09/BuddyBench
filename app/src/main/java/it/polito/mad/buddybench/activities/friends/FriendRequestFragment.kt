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
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * A fragment representing a list of Items.
 */
class FriendRequestFragment : Fragment() {

    // ** UI
    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var pbFriendRequests: ProgressBar

    // ** View Model
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    // ** Column count
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        friendsViewModel.getFriendRequests()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_request_list, container, false)

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

        // ** Data
        friendsViewModel.friendRequests.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                // Set the adapter
                with(rvFriendRequests) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = FriendRequestRecyclerViewAdapter(it, friendsViewModel)
                }
            }
        }
        return view
    }
}