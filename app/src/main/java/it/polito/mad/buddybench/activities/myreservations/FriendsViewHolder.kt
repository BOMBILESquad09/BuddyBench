package it.polito.mad.buddybench.activities.myreservations

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ReservationViewModel

class FriendsViewHolder(val v: View, private val inNotInvitedList: Boolean):  RecyclerView.ViewHolder(v) {
    private val profileIv: ImageView = v.findViewById(R.id.profile_iv)
    private val nicknameIv: TextView = v.findViewById(R.id.nickname_tv)
    private val profileCv: MaterialCardView = v.findViewById(R.id.profile_cv)


    fun bind(profile: Pair<Profile, Boolean>, onChange: (Profile) -> Unit){
        ((FragmentComponentManager.findActivity(v.context) as HomeActivity)).imageViewModel.getUserImage(profile.first.email,{

            profileIv.setImageResource(R.drawable.person)

        }){
            Glide.with(v.context)
                .load(it)
                .into(profileIv)
        }

        if(!inNotInvitedList)  {
            profileCv.checkedIcon = ContextCompat.getDrawable(v.context,R.drawable.close_circle)
            profileCv.checkedIconTint = ColorStateList.valueOf(ContextCompat.getColor(v.context, R.color.error))
        }

        nicknameIv.text = profile.first.nickname
        profileCv.isChecked = profile.second
        profileCv.setOnClickListener{
            onChange(profile.first)
        }

        profileCv.setOnLongClickListener {
            Utils.goToProfileFriend(
                FragmentComponentManager.findActivity(v.context) as HomeActivity,
                profile.first
            )
            true
        }

    }
}