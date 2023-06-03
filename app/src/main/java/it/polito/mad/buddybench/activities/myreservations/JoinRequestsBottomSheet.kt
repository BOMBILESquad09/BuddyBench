package it.polito.mad.buddybench.activities.myreservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.FriendRequestRecyclerViewAdapter
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.JoinRequestViewModel

class JoinRequestsBottomSheet(val reservation: ReservationDTO): BottomSheetDialogFragment() {

    val viewModel by activityViewModels<JoinRequestViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view  = inflater.inflate(R.layout.join_requests, container, false)
        val behaviour = BottomSheetBehavior.from(view.findViewById(R.id.standard_bottom_sheet))
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUpdatedReservation(reservation)

        /*val itemAnimator = object : DefaultItemAnimator() {
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
        }*/

        val callback: (profile: Profile) -> Unit = {
            Utils.goToProfileFriend(requireActivity() as HomeActivity, it)
        }
        val recyclerViewFriend = view.findViewById<RecyclerView>(R.id.recycler_join_requests)

        val noJoinRequest = view.findViewById<ConstraintLayout>(R.id.no_join_request)
        if (reservation.requests.isEmpty()) {
            noJoinRequest.visibility = View.VISIBLE
        } else {
            recyclerViewFriend.visibility = View.VISIBLE
            recyclerViewFriend.layoutManager = LinearLayoutManager(context)
            //recyclerViewFriend.itemAnimator = itemAnimator
            recyclerViewFriend.adapter = FriendRequestRecyclerViewAdapter(
                reservation.requests,
                null,
                viewModel,
                callback,
                {}
            )
        }

        viewModel.currentReservation.observe(this) {
            if(it == null) return@observe
            if(it.requests.isEmpty()) {
                noJoinRequest.visibility = View.VISIBLE
                recyclerViewFriend.visibility = View.GONE
            } else {
                noJoinRequest.visibility = View.GONE
                recyclerViewFriend.visibility = View.VISIBLE
                recyclerViewFriend.adapter = FriendRequestRecyclerViewAdapter(
                    it.requests,
                    null,
                    viewModel,
                    callback,
                    {}
                )
            }
        }

    }

}