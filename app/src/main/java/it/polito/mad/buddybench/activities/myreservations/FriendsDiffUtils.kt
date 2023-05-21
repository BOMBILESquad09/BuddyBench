package it.polito.mad.buddybench.activities.myreservations


import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.CourtDTO

class FriendsDiffUtils(
    private val lastFriends: List<Pair<Profile, Boolean>>,
    private val newFriends: List<Pair<Profile, Boolean>>

): DiffUtil.Callback(
){

    override fun getOldListSize(): Int {
        return lastFriends.size
    }

    override fun getNewListSize(): Int {
        return newFriends.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastFriends[oldItemPosition].first.email == newFriends[newItemPosition].first.email
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastFriends[oldItemPosition].second == newFriends[newItemPosition].second
    }

}