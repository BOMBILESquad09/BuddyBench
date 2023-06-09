package it.polito.mad.buddybench.activities.invitations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.persistence.dto.InvitationDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel

class InvitationAdapter(var invitations: List<ReservationDTO>,
                        private val onAccept: (ReservationDTO) -> Unit,
                        private val onDecline: (ReservationDTO) -> Unit,
                        private val isInvitation: Boolean = true,
                        private val findCourtViewModel: FindCourtViewModel?
    ) : RecyclerView.Adapter<InvitationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_invitation, parent, false)
        println("creatinggg view holder")
        return InvitationViewHolder(view, onAccept, onDecline, isInvitation, findCourtViewModel )
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        val invitation = invitations[position]
        holder.bind(invitation)
    }

    override fun getItemCount(): Int = invitations.size
}