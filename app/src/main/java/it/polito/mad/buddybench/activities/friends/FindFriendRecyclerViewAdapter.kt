package it.polito.mad.buddybench.activities.friends

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentFindFriendItemBinding
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class FindFriendRecyclerViewAdapter(private val values: List<Profile>,private val viewModel: FriendsViewModel) : RecyclerView.Adapter<FindFriendRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentFindFriendItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvName.text = item.fullName
        holder.tvUsername.text = item.nickname
        holder.btnAdd.setOnClickListener {
            viewModel.sendRequest(item.email) { updateAddBtn(holder, position) }
        }
    }

    override fun getItemCount(): Int = values.size

    private fun updateAddBtn(holder: ViewHolder, position: Int) {
        holder.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
        holder.btnAdd.text = holder.btnAdd.context.getString(R.string.added)
    }

    inner class ViewHolder(binding: FragmentFindFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvFriendName
        val tvUsername = binding.tvFriendUsername
        val ivImage = binding.ivFriendImage
        val btnAdd = binding.btnAddFriend

        override fun toString(): String {
            return super.toString() + " '" + tvName.text + "'"
        }
    }
}