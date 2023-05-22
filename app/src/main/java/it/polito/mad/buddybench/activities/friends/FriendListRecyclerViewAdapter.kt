package it.polito.mad.buddybench.activities.friends

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentFindFriendsListBinding
import it.polito.mad.buddybench.databinding.FragmentFriendListItemBinding
import it.polito.mad.buddybench.databinding.FragmentItemBinding
import it.polito.mad.buddybench.viewmodels.FriendsViewModel


class FriendListRecyclerViewAdapter(
    var values: List<Profile>,
    val viewModel: FriendsViewModel,
    val callback: (profile: Profile) -> Unit
) : RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(
            FragmentFriendListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentFriendListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvFriendName
        val tvUsername = binding.tvFriendUsername
        val ivImage = binding.ivFriendImage
        val btnRemove = binding.btnRemoveFriend


        fun bind(profile: Profile) {
            tvName.text = profile.fullName
            tvUsername.text = profile.nickname
            (FragmentComponentManager.findActivity(binding.root.context) as HomeActivity).imageViewModel.getUserImage(
                profile.email,
                {
                    ivImage.setImageResource(R.drawable.person)
                }) {
                val drawable = Glide.with(binding.root.context)
                    .load(it)
                    .submit().get()
                ivImage.setImageDrawable(drawable)
            }

            ivImage.setOnClickListener {
                callback(profile)
            }
            var sure = false
            btnRemove.setOnClickListener {
                if (sure == false) {
                    sure = true
                    btnRemove.text = "Are you sure?"
                } else {
                    viewModel.removeFriend(profile.email)
                }
            }

        }
    }

}