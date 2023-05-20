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
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class FindFriendFragment : Fragment() {

    // ** UI
    private lateinit var rvFindFriends: RecyclerView
    private lateinit var pbFindFriends: ProgressBar

    // ** View Model
    private val friendsViewModel by activityViewModels<FriendsViewModel>()

    // ** Column count
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_find_friends_list, container, false)
        rvFindFriends = view.findViewById(R.id.rv_find_friends)
        pbFindFriends = view.findViewById(R.id.pb_find_friends)

        // ** Loading state
        friendsViewModel.l.observe(viewLifecycleOwner) {
            if (it) {
                pbFindFriends.visibility = View.VISIBLE
                rvFindFriends.visibility = View.GONE
            } else {
                pbFindFriends.visibility = View.GONE
                rvFindFriends.visibility = View.VISIBLE
            }
        }
        with(rvFindFriends) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FindFriendRecyclerViewAdapter(listOf(), friendsViewModel)
        }
        // ** Data
        friendsViewModel.possibleFriends.observe(viewLifecycleOwner) {
            if (it != null) {

                with(rvFindFriends){
                    val oldList = (adapter as FindFriendRecyclerViewAdapter).values
                    val friendDiff = FriendListDiffUtils(oldList, it)
                    val diffs = DiffUtil.calculateDiff(friendDiff)
                    (adapter as FindFriendRecyclerViewAdapter).values = it
                    diffs.dispatchUpdatesTo(adapter!!)
                }

            }
        }

        return view
    }
}