package it.polito.mad.buddybench.activities.friends

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import it.polito.mad.buddybench.R

import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentFriendRequestItemBinding
import it.polito.mad.buddybench.viewmodels.FriendsViewModel

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class FriendRequestRecyclerViewAdapter(private val values: List<Profile>, private val viewModel: FriendsViewModel) : RecyclerView.Adapter<FriendRequestRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentFriendRequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.tvName.text = item.fullName
        holder.btnConfirm.setOnClickListener{ confirmRequest(item.email, holder.btnReject.context) }
        holder.btnReject.setOnClickListener { rejectRequest(item.email, holder.btnReject.context) }
    }

    override fun getItemCount(): Int = values.size

    private fun confirmRequest(email: String, context: Context) {
        viewModel.confirmRequest(email)
        Toast.makeText(context, "Friend request accepted", Toast.LENGTH_LONG).show()
        viewModel.getFriendRequests()
    }

    private fun rejectRequest(email: String, context: Context) {
        Toast.makeText(context, "Friend request declined", Toast.LENGTH_LONG).show()
        viewModel.rejectRequest(email)
        viewModel.friendRequests
    }

    inner class ViewHolder(binding: FragmentFriendRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName: TextView = binding.tvFriendRequestName
        val ivImage: ImageView = binding.ivFriendRequestImage
        val btnConfirm = binding.btnConfirmRequest
        val btnReject = binding.btnRejectRequest

        override fun toString(): String {
            return super.toString() + " '" + tvName.text + "'"
        }
    }

}