package it.polito.mad.buddybench.activities.friends

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentFindFriendItemBinding
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FriendsViewModel


class FindFriendRecyclerViewAdapter(
    var values: List<Profile>,
    private val viewModel: FriendsViewModel
) : RecyclerView.Adapter<FindFriendRecyclerViewAdapter.ViewHolder>() {

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
        holder.bind(item)

    }

    override fun getItemCount(): Int = values.size


    inner class ViewHolder(val binding: FragmentFindFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvFriendName
        val tvUsername = binding.tvFriendUsername
        val ivImage = binding.ivFriendImage

        val btnAdd = binding.btnAddFriend
        val btnAddimage = binding.btnAddFriendImage


        override fun toString(): String {
            return super.toString() + " '" + tvName.text + "'"
        }

        fun bind(profile: Profile) {
            tvName.text = profile.fullName
            tvUsername.text = profile.nickname



            updateAddBtn(profile.isPending)
            btnAdd.setOnClickListener {
                if (profile.isPending) {
                    viewModel.removeFriendRequest(profile.email) {
                    }
                } else {
                    viewModel.sendRequest(profile.email) {
                    }
                }
            }
            (FragmentComponentManager.findActivity(binding.root.context) as HomeActivity).imageViewModel.getUserImage(
                profile.email,
                {
                    ivImage.setImageResource(R.drawable.person)
                }) {

                Glide.with(binding.root.context)
                    .load(it)
                    .into(ivImage)

            }
            ivImage.setOnClickListener {
                (FragmentComponentManager.findActivity(binding.root.context) as HomeActivity).findViewById<View>(R.id.progress_bar).visibility = View.VISIBLE

                Utils.goToProfileFriend(
                    FragmentComponentManager.findActivity(binding.root.context) as HomeActivity,
                    profile
                )
            }

        }

        private fun updateAddBtn(state: Boolean) {
            this.btnAddimage.backgroundTintList = ColorStateList.valueOf(binding.root.context.getColor(android.R.color.transparent))
            if (state) {
                this.btnAddimage.backgroundTintList =
                        ColorStateList.valueOf(binding.root.context.getColor(R.color.disabled))
                this.btnAddimage.setImageResource(R.drawable.request_sent)
            } else {

                this.btnAdd.backgroundTintList =
                    ColorStateList.valueOf(binding.root.context.getColor(R.color.md_theme_light_primary))
                this.btnAddimage.setImageResource(R.drawable.add_friend)
            }

        }
    }
}