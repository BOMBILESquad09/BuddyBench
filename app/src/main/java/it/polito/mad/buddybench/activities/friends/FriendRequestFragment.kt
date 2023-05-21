package it.polito.mad.buddybench.activities.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
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
    private lateinit var emptyLL: LinearLayout
    private lateinit var emptyTv: TextView

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
        emptyLL = view.findViewById(R.id.empty)
        emptyTv = view.findViewById(R.id.tv_empty)
        emptyLL.visibility = View.GONE
        emptyTv.text = getString(R.string.no_friend_requests)
        // ** Loading state
        friendsViewModel.lRequests.observe(viewLifecycleOwner) {
            if (it) {
                pbFriendRequests.visibility = View.VISIBLE
                rvFriendRequests.visibility = View.GONE
            } else {

                pbFriendRequests.visibility = View.GONE
                if (friendsViewModel.friendRequests.value!!.isEmpty()) {
                    emptyLL.visibility = View.VISIBLE
                    rvFriendRequests.visibility = View.GONE
                } else {
                    emptyLL.visibility = View.GONE
                    rvFriendRequests.visibility = View.VISIBLE
                }
            }
        }



        with(rvFriendRequests) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = FriendRequestRecyclerViewAdapter(listOf(), friendsViewModel)
            itemAnimator = object : DefaultItemAnimator() {
                override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
                    if (holder != null) {
                        holder as FriendRequestRecyclerViewAdapter.ViewHolder
                        val animation = if (holder.accepted) {
                            AnimationUtils.loadAnimation(
                                holder.itemView.context,
                                R.anim.slide_out_left
                            )
                        } else {
                            AnimationUtils.loadAnimation(
                                holder.itemView.context,
                                android.R.anim.slide_out_right
                            )
                        }
                        holder.itemView.startAnimation(animation)

                    }
                    return super.animateRemove(holder)
                }
            }
        }

        // ** Data
        friendsViewModel.friendRequests.observe(viewLifecycleOwner) {
            if (it != null) {

                with(rvFriendRequests) {
                    val oldList = friendsViewModel.oldFriendsRequests
                    val friendDiff = FriendListDiffUtils(oldList, it)
                    val diffs = DiffUtil.calculateDiff(friendDiff)

                    (adapter as FriendRequestRecyclerViewAdapter).values = it
                    diffs.dispatchUpdatesTo(adapter!!)
                    rvFriendRequests.viewTreeObserver.addOnGlobalLayoutListener(
                        object : OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                println("diocaneeeeeeeee")
                                if ((rvFriendRequests.adapter as FriendRequestRecyclerViewAdapter).values.isEmpty()) {
                                    emptyLL.visibility = View.VISIBLE
                                    rvFriendRequests.visibility = View.GONE
                                } else {
                                    emptyLL.visibility = View.GONE
                                    rvFriendRequests.visibility = View.VISIBLE
                                }
                                rvFriendRequests.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        })
                }

            }

        }
    }


}

