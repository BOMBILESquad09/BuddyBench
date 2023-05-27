package it.polito.mad.buddybench.activities.friends

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

import it.polito.mad.buddybench.activities.friends.placeholder.PlaceholderContent.PlaceholderItem
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentFriendRequestItemBinding
import it.polito.mad.buddybench.viewmodels.FriendsViewModel
import kotlinx.coroutines.runBlocking

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class FriendRequestRecyclerViewAdapter(
    var values: List<Profile>,
    private val viewModel: FriendsViewModel,
    val callback: (profile: Profile) -> Unit,
    val onLast: () -> Unit
) : RecyclerView.Adapter<FriendRequestRecyclerViewAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(
            FragmentFriendRequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item)



    }


    override fun getItemCount(): Int = values.size

    private fun confirmRequest(email: String, context: Context, onSuccess: () -> Unit) {
        viewModel.confirmRequest(email, onSuccess)
        //Toast.makeText(context, "Friend request accepted", Toast.LENGTH_LONG).show()
    }

    private fun rejectRequest(email: String, context: Context, onSuccess: () -> Unit) {
        viewModel.rejectRequest(email, onSuccess)
        //Toast.makeText(context, "Friend request declined", Toast.LENGTH_LONG).show()
    }

    inner class RequestViewHolder(val binding: FragmentFriendRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvName: TextView = binding.tvFriendRequestName
        val ivImage: ImageView = binding.ivFriendRequestImage
        val btnConfirm = binding.btnConfirmRequest
        val btnReject = binding.btnRejectRequest
        var accepted: Boolean = false

        override fun toString(): String {
            return super.toString() + " '" + tvName.text + "'"
        }


        fun bind(profile: Profile) {
            tvName.text = profile.fullName
            ((FragmentComponentManager.findActivity(binding.root.context) as HomeActivity)).imageViewModel.getUserImage(
                profile.email,
                {
                    ivImage.setImageResource(R.drawable.person)
                }) {
                Glide.with(binding.root.context)
                    .load(it)
                    .into(ivImage)
            }
            ivImage.setOnClickListener {
                callback(profile)
            }
            btnConfirm.setOnClickListener {
                accepted = true
                binding.root.postOnAnimation {
                    if(values.size != 1) return@postOnAnimation
                    println("rimosssoooooooooooooooooooo")
                    onLast()
                }
                confirmRequest(profile.email, btnReject.context) {
                }
            }

            btnReject.setOnClickListener {
                accepted = false

                rejectRequest(profile.email, btnReject.context) {

                }

            }

        }
    }


}