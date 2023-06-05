package it.polito.mad.buddybench.activities.friends

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FriendsViewModel


class FriendListFragment(val context: HomeActivity) : Fragment(R.layout.fragment_friend_request_list) {

    private var columnCount = 1
    private lateinit var rvFriendList: RecyclerView
    private lateinit var pbFriendList: LinearLayout
    private lateinit var emptyLL: LinearLayout
    private lateinit var emptyTv: TextView
    private val friendsViewModel by activityViewModels<FriendsViewModel>()
    private lateinit var swipeRefresh : SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            context.friendsViewModel.refreshAll{swipeRefresh.isRefreshing = false}
        }

        // Set the adapter
        rvFriendList = view.findViewById(R.id.rv_friend_requests)
        pbFriendList = view.findViewById(R.id.pb_friend_requests)
        emptyLL = view.findViewById(R.id.empty)
        emptyTv = view.findViewById(R.id.tv_empty)
        emptyLL.visibility = View.GONE
        emptyTv.text = getString(R.string.you_have_no_friends_yet)

        // ** Loading state
        friendsViewModel.lFriends.observe(viewLifecycleOwner) {
            if ( it == null  || it) {
                pbFriendList.visibility = View.VISIBLE
            } else {
                pbFriendList.visibility = View.GONE
            }
        }

        val callback: (profile: Profile) -> Unit = {
            requireActivity().findViewById<View>(R.id.progress_bar).visibility = View.VISIBLE

            Utils.goToProfileFriend(context, it)
        }

        with(rvFriendList) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FriendListRecyclerViewAdapter(listOf(), friendsViewModel, callback)
        }
        // ** Data
        friendsViewModel.friends.observe(viewLifecycleOwner) {
            if (it != null) {
                with(rvFriendList) {
                    val oldList = friendsViewModel.oldFriends
                    val friendDiff = FriendListDiffUtils(oldList, it)
                    val diffs = DiffUtil.calculateDiff(friendDiff)

                    (adapter as FriendListRecyclerViewAdapter).values = it
                    diffs.dispatchUpdatesTo(adapter!!)


                    if ((adapter as FriendListRecyclerViewAdapter).values.isEmpty()) {
                        emptyLL.visibility = View.VISIBLE
                        rvFriendList.visibility = View.GONE
                    }
                    else{
                        emptyLL.visibility = View.GONE
                        rvFriendList.visibility = View.VISIBLE
                    }


                }
            }
        }
    }
}


