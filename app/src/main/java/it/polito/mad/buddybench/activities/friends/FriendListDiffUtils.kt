package it.polito.mad.buddybench.activities.friends



import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.CourtDTO

class FriendListDiffUtils(
    private val lastFriends: List<Profile>,
    private val newFriends: List<Profile>

): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastFriends.size
    }

    override fun getNewListSize(): Int {
        return newFriends.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastFriends[oldItemPosition].email == newFriends[newItemPosition].email
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        return lastFriends[oldItemPosition].email == newFriends[newItemPosition].email
                && lastFriends[oldItemPosition].isPending == newFriends[newItemPosition].isPending
    }

}