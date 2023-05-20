package it.polito.mad.buddybench.activities.invitations

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import java.time.format.DateTimeFormatter

class InvitationViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    private val cardInvitation: CardView = view.findViewById(R.id.card_invitation)
    private val cardInner: CardView = view.findViewById(R.id.card_inner)
    private val inviterPicture: ImageView = view.findViewById(R.id.inviter_picture)
    private val inviteText: TextView = view.findViewById(R.id.invite_text)
    private val courtName: TextView = view.findViewById(R.id.card_invitation_court)
    private val address: TextView = view.findViewById(R.id.card_invitation_address)
    private val date: TextView = view.findViewById(R.id.card_invitation_date)
    private val slot: TextView = view.findViewById(R.id.card_invitation_hours)

    var slotFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    var dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    fun bind(invitation: ReservationDTO){

        //set color bg
        cardInvitation.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(invitation.court.sport), view.context))
        cardInner.setCardBackgroundColor(Sports.getSportColor(Sports.valueOf(invitation.court.sport),view.context))

        //set inviter
        ((FragmentComponentManager.findActivity(view.context) as HomeActivity)).imageViewModel.getUserImage(invitation.userOrganizer.email,{

            inviterPicture.setImageResource(R.drawable.person)

        }){
            Glide.with(view.context)
                .load(it)
                .into(inviterPicture)
        }
        //set invite text
        inviteText.text = "${invitation.userOrganizer.name} invites you to play ${invitation.court.sport.lowercase()}!"

        //set court
        courtName.text = invitation.court.name

        //set address
        address.text = invitation.court.address

        //set date
        date.text = "${invitation.date.format(dateFormatter)}"

        //set hours
        slot.text = "${invitation.startTime.format(slotFormatter)} - ${invitation.endTime.format(slotFormatter)}"

    }
}