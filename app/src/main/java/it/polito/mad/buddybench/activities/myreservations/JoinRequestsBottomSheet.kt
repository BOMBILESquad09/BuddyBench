package it.polito.mad.buddybench.activities.myreservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.compose.runtime.remember
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.FriendListDiffUtils
import it.polito.mad.buddybench.activities.friends.FriendListRecyclerViewAdapter
import it.polito.mad.buddybench.activities.friends.FriendRequestRecyclerViewAdapter
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.JoinRequestViewModel

class JoinRequestsBottomSheet(val reservation: ReservationDTO) : BottomSheetDialogFragment() {

    val viewModel by activityViewModels<JoinRequestViewModel>()
    private var columnCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.join_requests, container, false)
        val behaviour = BottomSheetBehavior.from(view.findViewById(R.id.standard_bottom_sheet))
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUpdatedReservation(reservation)
        val callback: (profile: Profile) -> Unit = {
            Utils.goToProfileFriend(requireActivity() as HomeActivity, it)
        }
        val recyclerViewFriend = view.findViewById<RecyclerView>(R.id.recycler_join_requests)
        val noJoinRequest = view.findViewById<ConstraintLayout>(R.id.no_join_request)
        recyclerViewFriend.layoutManager = LinearLayoutManager(context)
        recyclerViewFriend.adapter = FriendRequestRecyclerViewAdapter(
            reservation.requests,
            null,
            viewModel,
            callback,
        ) {}

        if (reservation.requests.isEmpty()) {
            recyclerViewFriend.visibility = View.GONE
            noJoinRequest.visibility = View.VISIBLE
        } else {
            recyclerViewFriend.visibility = View.VISIBLE
            noJoinRequest.visibility = View.GONE
        }



        viewModel.currentReservation.observe(this) {
            if (it == null) return@observe
            val oldList = (recyclerViewFriend.adapter as FriendRequestRecyclerViewAdapter).values

            val friendDiff = FriendListDiffUtils(oldList, it.requests)
            val diffs = DiffUtil.calculateDiff(friendDiff)
            (recyclerViewFriend.adapter as FriendRequestRecyclerViewAdapter).values = it.requests
            diffs.dispatchUpdatesTo(recyclerViewFriend.adapter!!)
            if (it.requests.isEmpty()) {
                val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
                val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
                recyclerViewFriend.postOnAnimation {
                    recyclerViewFriend.visibility = View.GONE
                    noJoinRequest.postOnAnimation {
                        noJoinRequest.visibility = View.VISIBLE
                    }
                    noJoinRequest.startAnimation(fadeIn)

                }
                fadeIn.duration = 400
                fadeOut.duration = fadeIn.duration
                recyclerViewFriend.startAnimation(fadeOut)
            } else {
                recyclerViewFriend.visibility = View.VISIBLE
                noJoinRequest.visibility = View.GONE
            }
        }
    }
}



