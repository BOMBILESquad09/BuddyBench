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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * A fragment representing a list of Items.
 */
class FriendRequestFragment : Fragment(R.layout.fragment_friend_request_list) {

    // ** UI
    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var pbFriendRequests: LinearLayout
    private lateinit var emptyLL: LinearLayout
    private lateinit var emptyTv: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

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
        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            (activity as HomeActivity).friendsViewModel.refreshAll {
                swipeRefresh.isRefreshing = false
            }
        }
        // ** Loading state
        friendsViewModel.lRequests.observe(viewLifecycleOwner) {
            if (it == null || it) {
                pbFriendRequests.visibility = View.VISIBLE
                //rvFriendRequests.visibility = View.GONE
            } else {

                pbFriendRequests.visibility = View.GONE
                /*if (friendsViewModel.friendRequests.value!!.isEmpty()) {
                    emptyLL.visibility = View.VISIBLE
                    rvFriendRequests.visibility = View.GONE
                } else {
                    emptyLL.visibility = View.GONE
                    rvFriendRequests.visibility = View.VISIBLE
                }*/
            }
        }

        val callback: (profile: Profile) -> Unit = {
            Utils.goToProfileFriend(activity as HomeActivity, it)
        }




        with(rvFriendRequests) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            adapter = FriendRequestRecyclerViewAdapter(listOf(), friendsViewModel, callback
            ) { }
            itemAnimator = object : DefaultItemAnimator() {
                override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
                    if (holder != null) {
                        holder as FriendRequestRecyclerViewAdapter.RequestViewHolder
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
                        holder.binding.root.startAnimation(animation)


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

                    if ((rvFriendRequests.adapter as FriendRequestRecyclerViewAdapter).values.isEmpty()) {
                        println("sto qui dsdsdsn")
                        rvFriendRequests.postOnAnimation {
                            println("recu")
                            rvFriendRequests.visibility = View.GONE
                            emptyLL.postOnAnimation {
                                println("empty")
                                emptyLL.visibility = View.VISIBLE
                            }
                            emptyLL.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))

                        }
                        rvFriendRequests.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                    } else {
                        emptyLL.visibility = View.GONE
                        rvFriendRequests.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

}



