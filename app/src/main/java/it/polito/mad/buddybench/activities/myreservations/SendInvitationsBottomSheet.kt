package it.polito.mad.buddybench.activities.myreservations

import android.content.Context
import android.icu.util.LocaleData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Tabs
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.InvitationsViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale

class SendInvitationsBottomSheet(
    private val reservationDTO: ReservationDTO,
    private val invited: Boolean = false
) : BottomSheetDialogFragment() {


    private val userViewModel by activityViewModels<UserViewModel>()
    private val invitationViewModel by activityViewModels<InvitationsViewModel>()
    private val reservationViewModel by activityViewModels<ReservationViewModel>()
    private val expired =
        (LocalDateTime.of(reservationDTO.date, reservationDTO.endTime) < LocalDateTime.now())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.reservation_friend, container, false)
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
            invitationViewModel.sendInvitations(
                reservationDTO,
                reservationViewModel.notInvitedFriends.value!!.filter { it.second }
                    .map { it.first.email }) {
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }

        val pendingButton = view.findViewById<Button>(R.id.remove_pending_button)
        pendingButton.setOnClickListener {
            invitationViewModel.removeInvitations(
                reservationDTO,
                reservationViewModel.pendingFriends.value!!.filter { it.second }
                    .map { it.first.email }) {
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }

        val acceptedButton = view.findViewById<Button>(R.id.remove_accepted_button)
        acceptedButton.setOnClickListener {
            invitationViewModel.removeAcceptedInvitations(
                reservationDTO,
                reservationViewModel.acceptedFriends.value!!.filter { it.second }
                    .map { it.first.email }) {
                reservationViewModel.getReservation(reservationDTO.id)
            }
        }


        recyclerViewNotInvited.layoutManager =
            LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }
        recyclerViewPending.layoutManager =
            LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }
        recyclerViewAccepted.layoutManager =
            LinearLayoutManager(context).let { it.orientation = HORIZONTAL; it }
        //aggiungere richieste di partecipazione


        recyclerViewNotInvited.adapter =
            FriendsAdapter(reservationViewModel.notInvitedFriends.value ?: listOf(), true) {
                if (!invited)
                    reservationViewModel.updateNotInvitedFriends(it)
            }
        recyclerViewPending.adapter =
            FriendsAdapter(reservationViewModel.pendingFriends.value ?: listOf(), false) {
                if (!invited)
                    reservationViewModel.updatePendingFriends(it)
            }
        val res: ReservationDTO? = if(invited)  reservationDTO else null
        recyclerViewAccepted.adapter =
            FriendsAdapter(reservationViewModel.acceptedFriends.value ?: listOf(), false, res) {
                if (!invited)
                    reservationViewModel.updateAcceptedFriends(it)
            }



        userViewModel.getUser().observe(this) {
            reservationViewModel.profile = it
            reservationViewModel.getReservation(reservationDTO.id, !invited)
        }



        reservationViewModel.acceptedFriends.observe(this) {
            if (it != null) {
                val diffUtilsAccepted =
                    FriendsDiffUtils(reservationViewModel.oldAcceptedFriends, it)
                val diffsAccepted = DiffUtil.calculateDiff(diffUtilsAccepted)

                (recyclerViewAccepted.adapter as FriendsAdapter).friends =
                    reservationViewModel.acceptedFriends.value!!
                diffsAccepted.dispatchUpdatesTo(recyclerViewAccepted.adapter!!)
                if (it.isEmpty()) {
                    emptyAccepted.visibility = View.VISIBLE
                } else {
                    emptyAccepted.visibility = View.GONE
                }
            }
        }

        reservationViewModel.pendingFriends.observe(this) {
            if (it != null) {
                val diffUtilsPending = FriendsDiffUtils(reservationViewModel.oldPendingFriends, it)
                val diffsPending = DiffUtil.calculateDiff(diffUtilsPending)
                (recyclerViewPending.adapter as FriendsAdapter).friends = it
                diffsPending.dispatchUpdatesTo(recyclerViewPending.adapter!!)
                if (it.isEmpty()) {
                    emptyPending.visibility = View.VISIBLE
                } else {
                    emptyPending.visibility = View.GONE
                }
            }
        }



        if (!invited) {
            reservationViewModel.notInvitedFriends.observe(this) {
                val diffUtils = FriendsDiffUtils(reservationViewModel.oldNotInvitedFriends, it)
                val diffs = DiffUtil.calculateDiff(diffUtils)
                (recyclerViewNotInvited.adapter as FriendsAdapter).friends = it
                diffs.dispatchUpdatesTo(recyclerViewNotInvited.adapter!!)
                if (it.isEmpty()) {
                    emptyNotInvited.visibility = View.VISIBLE
                } else {
                    emptyNotInvited.visibility = View.GONE
                }
            }

            //aggiungere persone che hanno richiesto
        }




        fun setForInvited() {
            if (invited) {
                view.findViewById<LinearLayout>(R.id.invite_friends_ll).visibility = View.GONE
                view.findViewById<LinearLayout>(R.id.pending_friends_ll).visibility = View.GONE
                acceptedButton.visibility = View.GONE
                pendingButton.visibility = View.GONE
                val removeButton = view.findViewById<MaterialButton>(R.id.remove_invite)
                val textSection = view.findViewById<TextView>(R.id.textView10)
                textSection.text = "Participants"
                if(LocalDateTime.of(reservationDTO.date, reservationDTO.startTime) > LocalDateTime.now()){
                    removeButton.visibility = View.VISIBLE
                    removeButton.setOnClickListener {

                        val dialogCard =
                            LayoutInflater.from(context).inflate(R.layout.confirm_cancel, null)
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this.requireContext())
                        dialogCard.findViewById<TextView>(R.id.confirm).text = "Confirm"
                        builder.setView(dialogCard)
                        val dialog: AlertDialog = builder.create()
                        val titleView = dialogCard.findViewById<TextView>(R.id.title)
                        titleView.text = "Cancel participation"
                        val bodyView = dialogCard.findViewById<TextView>(R.id.text_problem)
                        bodyView.text = "Are you sure to cancel your participation?"
                        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.setCancelable(true)
                        dialogCard.findViewById<View>(R.id.confirm).setOnClickListener {
                            invitationViewModel.removeAcceptedInvitations(
                                reservationDTO,
                                listOf(Firebase.auth.currentUser!!.email!!)
                            ) {
                                reservationViewModel.getReservation(reservationDTO.id)
                                reservationViewModel.getAllByUser()
                                dialog.dismiss()
                                this.dismiss()
                            }
                        }

                        dialogCard.findViewById<View>(R.id.cancel).setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()

                    }

                }

            }
        }


        fun setExpired() {
            acceptedButton.visibility = View.GONE
            pendingButton.visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.invite_friends_ll).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.pending_friends_ll).visibility = View.GONE
            notInvitedButton.visibility = View.GONE

        }


        setForInvited()
        if (expired) setExpired()

    }


}