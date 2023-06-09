package it.polito.mad.buddybench.activities.invitations

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.opengl.Visibility
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.animation.fadeIn
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.profile.RemoveFriendDialog
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import net.cachapa.expandablelayout.ExpandableLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.exp

class InvitationViewHolder(
    val view: View, val onAccept: (ReservationDTO) -> Unit,
    val onDecline: (ReservationDTO) -> Unit,
    val isInvitation: Boolean,
    val findCourtViewModel: FindCourtViewModel?
) : RecyclerView.ViewHolder(view) {

    private val cardInvitation: CardView = view.findViewById(R.id.card_invitation)
    private val cardInner: ExpandableLayout = view.findViewById(R.id.card_inner)
    private val inviterPicture: ImageView = view.findViewById(R.id.inviter_picture)
    private val inviteText: TextView = view.findViewById(R.id.invite_text)
    private val courtName: TextView = view.findViewById(R.id.card_invitation_court)
    private val address: TextView = view.findViewById(R.id.card_invitation_address)
    private val dayLayout: View = view.findViewById(R.id.day_layout)
    private val date: TextView = view.findViewById(R.id.card_invitation_date)
    private val slot: TextView = view.findViewById(R.id.card_invitation_hours)
    private val acceptButton: TextView = view.findViewById(R.id.accept_btn)
    private val declineButton: TextView = view.findViewById(R.id.decline_btn)

    private val expandButton: ImageView = view.findViewById(R.id.expand_icon)

    var slotFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    lateinit var invitation: ReservationDTO

    fun bind(invitationDTO: ReservationDTO) {

        invitation = invitationDTO
        //set color bg
        cardInvitation.setCardBackgroundColor(
            Sports.getSportColor(
                Sports.valueOf(invitation.court.sport),
                view.context
            )
        )
        //cardInner.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(invitation.court.sport),view.context))

        //set inviter
        ((FragmentComponentManager.findActivity(view.context) as HomeActivity)).imageViewModel.getUserImage(
            invitation.userOrganizer.email,
            {
                inviterPicture.setImageResource(R.drawable.person)
            }) {
            Glide.with(view.context)
                .load(it)
                .placeholder(R.drawable.loading)
                .error(R.drawable.person)
                .into(inviterPicture)
        }

        inviterPicture.setOnClickListener {

            Utils.goToProfileFriend(
                FragmentComponentManager.findActivity(view.context) as AppCompatActivity,
                invitation.userOrganizer
            )
        }
        //set invite text
        inviteText.text = "${invitation.userOrganizer.nickname} invited you to play ${
            invitation.court.sport.lowercase().capitalize(Locale.ENGLISH)
        }!"

        //set court
        courtName.text = invitation.court.name

        //set address
        address.text = "${invitation.court.location}, ${invitation.court.address}"

        //set date
        date.text = "${
            invitation.date.format(
                DateTimeFormatter.ofPattern(
                    "EEEE, d MMMM y",
                    Locale.ENGLISH
                )
            )
        }"
        //set hours
        slot.text = "${invitation.startTime.format(slotFormatter)} - ${
            invitation.endTime.format(slotFormatter)
        }"
        if (isInvitation) {
            acceptButton.setOnClickListener {
                onAccept(invitation)
            }

            declineButton.setOnClickListener {
                onDecline(invitation)
            }
        }

        cardInner.duration = 400
        expandButton.setOnClickListener {


            if (!cardInner.isExpanded) {
                cardInner.expand()
                cardInner.startAnimation(
                    AnimationUtils.loadAnimation(
                        view.context,
                        android.R.anim.fade_in
                    )
                )
                expandButton.setImageResource(R.drawable.expand_up)
            } else {
                cardInner.startAnimation(
                    AnimationUtils.loadAnimation(
                        view.context,
                        android.R.anim.fade_out
                    )
                )
                cardInner.collapse()
                expandButton.setImageResource(R.drawable.expand_down)
            }
        }

        if (!isInvitation) {
            cardInner.expand(false)
            dayLayout.visibility = View.GONE
            expandButton.visibility = View.GONE
            inviteText.text = "${invitation.userOrganizer.nickname} is organizing a ${
                invitation.court.sport.lowercase().capitalize(Locale.ENGLISH)
            } game!"
            inviteText.maxLines = 3

            declineButton.visibility = View.GONE
            val frame = view.findViewById<FrameLayout>(R.id.frame_layout)
            frame.visibility = View.GONE
            val cardDecline = view.findViewById<FrameLayout>(R.id.card_decline)
            cardDecline.visibility = View.GONE

            acceptButton.text = " Request to join "

            val layout = view.findViewById<LinearLayout>(R.id.linearLayout)
            layout.gravity = Gravity.CENTER


            acceptButton.setOnClickListener {

                //if request not sent
                if (!invitation.requests.map { it.email }
                        .contains(Firebase.auth.currentUser!!.email!!)) {
                    findCourtViewModel!!.sendJoinRequest(invitation) {

                    }
                } else {
                    val bottomSheet = CancelRequestJoinDialog(
                        invitation
                    ) { }
                    bottomSheet.show(
                        (FragmentComponentManager.findActivity(view.context) as AppCompatActivity).supportFragmentManager,
                        ""
                    )
                }
            }
            setJoinRequestCard()
        }

    }

    private fun setJoinRequestCard() {
        if (invitation.requests.map { it.email }
                .contains(Firebase.auth.currentUser!!.email!!)) {
            acceptButton.text = "Request sent"
        } else {
            acceptButton.text = " Request to join "
        }
    }

}


