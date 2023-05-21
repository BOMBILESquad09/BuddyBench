package it.polito.mad.buddybench.activities.friends

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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.viewmodels.FriendsViewModel


class FriendListFragment : Fragment(R.layout.fragment_friend_request_list) {

    private var columnCount = 1
    private lateinit var rvFriendList: RecyclerView
    private lateinit var pbFriendList: ProgressBar
    private lateinit var emptyLL: LinearLayout
    private lateinit var emptyTv: TextView
    private val friendsViewModel by activityViewModels<FriendsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Set the adapter
        rvFriendList = view.findViewById(R.id.rv_friend_requests)
        pbFriendList = view.findViewById(R.id.pb_friend_requests)
        emptyLL = view.findViewById(R.id.empty)
        emptyTv = view.findViewById(R.id.tv_empty)
        emptyLL.visibility = View.GONE
        emptyTv.text = getString(R.string.you_have_no_friends_yet)

        // ** Loading state
        friendsViewModel.lRequests.observe(viewLifecycleOwner) {
            if (it) {
                pbFriendList.visibility = View.VISIBLE
                rvFriendList.visibility = View.GONE
                emptyLL.visibility = View.GONE

            } else {
                pbFriendList.visibility = View.GONE
                emptyLL.visibility = View.VISIBLE
                rvFriendList.visibility = View.VISIBLE


            }
        }

        with(rvFriendList) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FriendListRecyclerViewAdapter(listOf(), friendsViewModel)
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
                    if (friendsViewModel.l.value != true) {

                        if (friendsViewModel.friends.value!!.isEmpty()) {
                            rvFriendList.visibility = View.GONE
                            emptyLL.visibility = View.VISIBLE

                        } else {
                            rvFriendList.visibility = View.VISIBLE
                            emptyLL.visibility = View.GONE
                        }


                    }

                }
            }
        }
    }
}


