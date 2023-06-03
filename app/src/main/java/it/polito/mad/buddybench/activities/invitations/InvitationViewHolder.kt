package it.polito.mad.buddybench.activities.invitations

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.opengl.Visibility
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
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
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.profile.RemoveFriendDialog
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import net.cachapa.expandablelayout.ExpandableLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.exp

class InvitationViewHolder(val view: View, val onAccept: (ReservationDTO) -> Unit,
                           val onDecline: (ReservationDTO) -> Unit,
                           val isInvitation: Boolean

                           ): RecyclerView.ViewHolder(view) {

    private val cardInvitation: CardView = view.findViewById(R.id.card_invitation)
    private val cardInner: ExpandableLayout = view.findViewById(R.id.card_inner)
    private val inviterPicture: ImageView = view.findViewById(R.id.inviter_picture)
    private val inviteText: TextView = view.findViewById(R.id.invite_text)
    private val courtName: TextView = view.findViewById(R.id.card_invitation_court)
    private val address: TextView = view.findViewById(R.id.card_invitation_address)
    private val date: TextView = view.findViewById(R.id.card_invitation_date)
    private val slot: TextView = view.findViewById(R.id.card_invitation_hours)
    private val acceptButton: TextView = view.findViewById(R.id.accept_btn)
    private val declineButton: TextView = view.findViewById(R.id.decline_btn)

    private val expandButton: ImageView = view.findViewById(R.id.expand_icon)

    var slotFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    fun bind(invitation: ReservationDTO){

        //set color bg
        cardInvitation.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(invitation.court.sport), view.context))
        //cardInner.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(invitation.court.sport),view.context))

        //set inviter
        ((FragmentComponentManager.findActivity(view.context) as HomeActivity)).imageViewModel.getUserImage(invitation.userOrganizer.email,{
            inviterPicture.setImageResource(R.drawable.person)
        }){
            Glide.with(view.context)
                .load(it)
                .placeholder(R.drawable.loading)
                .error(R.drawable.person)
                .into(inviterPicture)
        }

        inviterPicture.setOnClickListener {

            Utils.goToProfileFriend(FragmentComponentManager.findActivity(view.context) as AppCompatActivity,
            invitation.userOrganizer)
        }
        //set invite text
        inviteText.text = "${invitation.userOrganizer.nickname} invited you to play ${invitation.court.sport.lowercase().capitalize(Locale.ENGLISH)}!"

        //set court
        courtName.text = invitation.court.name

        //set address
        address.text = "${invitation.court.location}, ${invitation.court.address}"

        //set date
        date.text = "${invitation.date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y", Locale.ENGLISH))}"
        //set hours
        slot.text = "${invitation.startTime.format(slotFormatter)} - ${invitation.endTime.format(slotFormatter)}"

        acceptButton.setOnClickListener {
            onAccept(invitation)
        }

        declineButton.setOnClickListener {
            onDecline(invitation)
        }

        cardInner.duration = 400
        expandButton.setOnClickListener{
            if (!cardInner.isExpanded) {
                cardInner.expand()
                cardInner.startAnimation(AnimationUtils.loadAnimation(view.context, android.R.anim.fade_in))
                expandButton.setImageResource(R.drawable.expand_up)
            } else {
                cardInner.startAnimation(AnimationUtils.loadAnimation(view.context, android.R.anim.fade_out))
                cardInner.collapse()
                expandButton.setImageResource(R.drawable.expand_down)
            }
        }

        if(!isInvitation){
            inviteText.text = "${invitation.userOrganizer.nickname} is organizing a ${invitation.court.sport.lowercase().capitalize(Locale.ENGLISH)} game!"
            inviteText.maxLines = 3

            declineButton.visibility = View.GONE

            acceptButton.text = "Join"
            acceptButton.setPadding(120,10,120,10)

            acceptButton.setOnClickListener{
                //if request not sent
                acceptButton.text = "Cancel request"
                acceptButton.setTextColor(view.context.getColor(R.color.error))


                val bottomSheet = CancelRequestJoinDialog()
                bottomSheet.show((FragmentComponentManager.findActivity(view.context) as AppCompatActivity).supportFragmentManager, "")
            }
        }

    }

    private fun setInvitationCard(){

    }
}