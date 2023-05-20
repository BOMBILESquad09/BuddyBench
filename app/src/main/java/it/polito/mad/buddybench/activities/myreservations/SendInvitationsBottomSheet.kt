package it.polito.mad.buddybench.activities.myreservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel

class SendInvitationsBottomSheet(
    private val reservationDTO: ReservationDTO,
    private val invited: Boolean = false): SuperBottomSheetFragment() {


    private val userViewModel by activityViewModels<UserViewModel>()
    private val invitationViewModel by viewModels<InvitationsViewModel>()
    private val reservationViewModel by viewModels<ReservationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.reservation_friend, container, false)
    }


    override fun isSheetAlwaysExpanded(): Boolean {
        return true
    }



    override fun getExpandedHeight(): Int {
        return -2
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewNotInvited = view.findViewById<RecyclerView>(R.id.not_invited_friends_rv)
        val recyclerViewPending = view.findViewById<RecyclerView>(R.id.pending_friends_rv)
        val recyclerViewAccepted = view.findViewById<RecyclerView>(R.id.accepted_friends_rv)

        val emptyNotInvited = view.findViewById<TextView>(R.id.not_invited_friends_empty_tv)
        val emptyPending = view.findViewById<TextView>(R.id.pending_friends_empty_tv)
        val emptyAccepted = view.findViewById<TextView>(R.id.accepted_friends_empty_tv)

        val notInvitedButton = view.findViewById<Button>(R.id.invite_button)
        notInvitedButton.setOnClickListener {
            invitationViewModel.sendInvitations(reservationDTO, reservationViewModel.notInvitedFriends.value!!.filter { it.second }.map { it.first.email }){
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }

        val pendingButton = view.findViewById<Button>(R.id.remove_pending_button)
        pendingButton.setOnClickListener {
            invitationViewModel.removeInvitations(reservationDTO, reservationViewModel.pendingFriends.value!!.filter { it.second }.map { it.first.email }){
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }

        val acceptedButton = view.findViewById<Button>(R.id.remove_accepted_button)
        acceptedButton.setOnClickListener {
            invitationViewModel.removeAcceptedInvitations(reservationDTO, reservationViewModel.acceptedFriends.value!!.filter { it.second }.map { it.first.email }){
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }


        recyclerViewNotInvited.layoutManager = LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }
        recyclerViewPending.layoutManager = LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }
        recyclerViewAccepted.layoutManager = LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }

        recyclerViewNotInvited.adapter = FriendsAdapter(reservationViewModel.notInvitedFriends.value ?: listOf()){
            if(!invited)
                reservationViewModel.updateNotInvitedFriends(it)
        }
        recyclerViewPending.adapter = FriendsAdapter(reservationViewModel.pendingFriends.value ?: listOf()){
            if(!invited)
                reservationViewModel.updatePendingFriends(it)

        }
        recyclerViewAccepted.adapter = FriendsAdapter(reservationViewModel.acceptedFriends.value ?: listOf()){
            if(!invited)
                reservationViewModel.updateAcceptedFriends(it)
        }

        userViewModel.getUser().observe(this){
            reservationViewModel.profile = it
            reservationViewModel.getReservation(reservationDTO.id)
        }



        reservationViewModel.acceptedFriends.observe(this){
            if (it != null) {
                val diffUtilsAccepted = FriendsDiffUtils(reservationViewModel.oldAcceptedFriends, it)
                val diffsAccepted = DiffUtil.calculateDiff(diffUtilsAccepted)

                (recyclerViewAccepted.adapter as FriendsAdapter).friends = reservationViewModel.acceptedFriends.value!!

                diffsAccepted.dispatchUpdatesTo(recyclerViewAccepted.adapter!!)
                if(it.isEmpty()){
                    emptyAccepted.visibility = View.VISIBLE
                } else{
                    emptyAccepted.visibility = View.GONE
                }

            }
        }

        reservationViewModel.pendingFriends.observe(this){
            if (it != null) {
                val diffUtilsPending = FriendsDiffUtils(reservationViewModel.oldPendingFriends, it)
                val diffsPending = DiffUtil.calculateDiff(diffUtilsPending)
                (recyclerViewPending.adapter as FriendsAdapter).friends = it
                diffsPending.dispatchUpdatesTo(recyclerViewPending.adapter!!)
                if(it.isEmpty()){
                    emptyPending.visibility = View.VISIBLE
                } else{
                    emptyPending.visibility = View.GONE
                }
            }
        }
        if(!invited){
            reservationViewModel.notInvitedFriends.observe(this){
                val diffUtils = FriendsDiffUtils(reservationViewModel.oldNotInvitedFriends, it)
                val diffs = DiffUtil.calculateDiff(diffUtils)
                (recyclerViewNotInvited.adapter as FriendsAdapter).friends = it
                diffs.dispatchUpdatesTo(recyclerViewNotInvited.adapter!!)
                if(it.isEmpty()){
                    emptyNotInvited.visibility = View.VISIBLE
                } else{
                    emptyNotInvited.visibility = View.GONE
                }
            }
        }




        fun setForInvited(){
            if(invited){
                view.findViewById<LinearLayout>(R.id.invite_friends_ll).visibility = View.GONE
                acceptedButton.visibility = View.GONE
                pendingButton.visibility = View.GONE
                val removeButton = view.findViewById<MaterialButton>(R.id.remove_invite)
                removeButton.visibility = View.VISIBLE
                var confirm = 0
                removeButton.setOnClickListener {
                    if(confirm == 0){
                        confirm = 1
                        removeButton.text = "Confirm to remove?"
                    } else{
                        invitationViewModel.removeAcceptedInvitations(reservationDTO, listOf(Firebase.auth.currentUser!!.email!!)){
                            reservationViewModel.getReservation(reservationDTO.id)

                            ((FragmentComponentManager.findActivity(view.context) as AppCompatActivity).supportFragmentManager.findFragmentByTag(
                                Tabs.RESERVATIONS.name) as MyReservationsFragment).viewModel.getAllByUser()
                            this.dismiss()
                        }
                    }

                }
            }
        }

        setForInvited()

    }






}