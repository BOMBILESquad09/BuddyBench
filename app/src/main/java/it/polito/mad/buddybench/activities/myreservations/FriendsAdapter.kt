package it.polito.mad.buddybench.activities.myreservations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile

class FriendsAdapter(var friends : List<Pair<Profile, Boolean>>,
                     private val onChange: (Profile) -> Unit
): RecyclerView.Adapter<FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_simple_item, parent, false)
        return FriendsViewHolder(v)
    }



    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val reservation = friends[position]
        holder.bind(reservation, onChange)
    }

}