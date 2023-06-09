package it.polito.mad.buddybench.activities.invitations
import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO

class InvitationsDiffsUtils(
    private val oldInvitations: List<ReservationDTO>,
    private val newInvitations: List<ReservationDTO>

): DiffUtil.Callback(
){

    override fun getOldListSize(): Int {
        
        return oldInvitations.size
    }

    override fun getNewListSize(): Int {
        return newInvitations.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldInvitations[oldItemPosition].id == newInvitations[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        println("----sizes-----")
        println(oldInvitations[oldItemPosition].requests.size)
        println(newInvitations[newItemPosition].requests.size)
        return oldInvitations[oldItemPosition].date == newInvitations[newItemPosition].date
                &&  oldInvitations[oldItemPosition].endTime == newInvitations[newItemPosition].endTime
                && oldInvitations[oldItemPosition].startTime == newInvitations[newItemPosition].startTime
                && oldInvitations[oldItemPosition].requests.size == newInvitations[newItemPosition].requests.size

    }

}