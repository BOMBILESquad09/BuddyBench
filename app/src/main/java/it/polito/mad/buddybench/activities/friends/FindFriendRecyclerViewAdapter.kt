package it.polito.mad.buddybench.activities.friends

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
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


        override fun toString(): String {
            return super.toString() + " '" + tvName.text + "'"
        }

        fun bind(profile: Profile) {
            tvName.text = profile.fullName
            tvUsername.text = profile.nickname
            if (profile.isPending) {
                updateAddBtn()
            }
            (FragmentComponentManager.findActivity(binding.root.context) as HomeActivity).imageViewModel.getUserImage(profile.email,{
                ivImage.setImageResource(R.drawable.person)
            }){
                Glide.with(binding.root.context)
                    .load(it)
                    .into(ivImage)
            }
            btnAdd.setOnClickListener {
                viewModel.sendRequest(profile.email) {
                    Toast.makeText(
                        btnAdd.context,
                        "A friend request has been sent to ${profile.fullName}",
                        Toast.LENGTH_LONG
                    ).show()
                    updateAddBtn()
                }
            }
        }

        private fun updateAddBtn() {
            this.btnAdd.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            this.btnAdd.text = btnAdd.context.getString(R.string.request_sent)
        }
    }
}