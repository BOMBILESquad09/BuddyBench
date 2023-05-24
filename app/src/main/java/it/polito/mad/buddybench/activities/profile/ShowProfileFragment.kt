package it.polito.mad.buddybench.activities.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.activities.friends.FriendProfileActivity
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.viewmodels.FriendsViewModel
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject
import org.w3c.dom.Text


@AndroidEntryPoint
class ShowProfileFragment(
    var seeProfile: Boolean = false,
    var friendProfile: Profile? = null
) : Fragment(R.layout.show_profile) {

    lateinit var profile: Profile
    private val imageViewModel by activityViewModels<ImageViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val friendViewModel by activityViewModels<FriendsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!seeProfile && friendProfile == null) {
            userViewModel.user.observe(this) {
                if (it != null) {
                    profile = it
                    loadImage(it.imageUri)
                    setGUI(it.imageUri)
                }
            }
        } else {
            profile = friendProfile!!
            setGUI()
        }
    }


    fun setGUI(imageUri: Uri? = null) {

        val thisView = requireView()

        val fullNameTv = thisView.findViewById<TextView>(R.id.fullname_tv)
        fullNameTv.text = profile.fullName

        val nicknameTv = requireView().findViewById<TextView>(R.id.nickname_tv)
        nicknameTv.text = "@${profile.nickname}"

        val ageTv = thisView.findViewById<TextView>(R.id.age_tv)
        ageTv.text = getString(R.string.age).format(profile.age)

        val locationTv = thisView.findViewById<TextView>(R.id.location_tv)
        locationTv.text = profile.location

        val matchesPlayedTv = thisView.findViewById<TextView>(R.id.games_played)
        matchesPlayedTv.text = profile.getMatchesPlayed().toString()

        val matchesOrganizedTv = thisView.findViewById<TextView>(R.id.games_organized)
        matchesOrganizedTv.text = profile.getMatchesOrganized().toString()

        val reliabilityTv = thisView.findViewById<TextView>(R.id.reliability)
        reliabilityTv.text = getString(R.string.reliabilityValue).format(profile.reliability)

        val iv = thisView.findViewById<ImageView>(R.id.profile_image)
        loadImage(profile.imageUri)

        //resizeImageView(iv)

        if(imageUri == null)
            imageViewModel.getUserImage(profile.email, { iv.setImageResource(R.drawable.person) }) {
                val options: RequestOptions = RequestOptions()
                Glide.with(this)
                    .load(it)
                    .signature(ObjectKey(it))
                    .apply(
                        options.centerCrop()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.person)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform()
                    )
                    .error(R.drawable.person)
                    .into(iv)
            }

        val sportsRecyclerView = thisView.findViewById<RecyclerView>(R.id.sports_container)
        sportsRecyclerView.layoutManager = LinearLayoutManager(context).let {
            it.orientation = RecyclerView.HORIZONTAL
            it
        }

        userViewModel.setSports(profile.sports)

        sportsRecyclerView.adapter = SportsAdapter(userViewModel.sports, false)
        userViewModel.sports.observe(this) {
            if (it == null) return@observe
        }
        if (profile.sports.none { it.skill != Skills.NULL }) {
            thisView.findViewById<TextView>(R.id.empty_sports).visibility = View.VISIBLE
        } else {
            thisView.findViewById<TextView>(R.id.empty_sports).visibility = View.GONE
        }

        val buttonEdit = thisView.findViewById<ShapeableImageView>(R.id.button_to_edit)
        if (!seeProfile) {
            // Setting up the button for edit
            buttonEdit.setOnClickListener {
                editProfile()
            }
        } else {

            buttonEdit.visibility = View.GONE
            val profileTitle = thisView.findViewById<TextView>(R.id.profile_title)
            profileTitle.text = profile.nickname

            val arrow = thisView.findViewById<ShapeableImageView>(R.id.come_back)
            arrow.visibility = View.VISIBLE
            arrow.setOnClickListener {
                returnFromFriendProfile()
            }

            val friendButton = thisView.findViewById<MaterialButton>(R.id.friend_request_btn)
            friendButton.visibility = View.VISIBLE
            userViewModel.user.observe(this) {
                if (it == null) return@observe
                if (!it.isPending && !it.isFriend && !it.isRequesting) {
                    friendButton.text = "Add Friend"
                } else if (it.isPending) {
                    friendButton.text = "Cancel Request"
                } else if (it.isFriend) {
                    friendButton.text = "Friends"
                } else if (it.isRequesting) {
                    friendButton.text = "Accept Request"
                } else {
                    friendButton.text = "Remove Friend"
                }
                (requireActivity() as FriendProfileActivity).bundle.remove("profile")
                (requireActivity() as FriendProfileActivity).bundle.putString(
                    "profile",
                    it.toJSON().toString()
                )

            }
            friendButton.setOnClickListener {
                val profileFriend = userViewModel.user.value!!
                if (!profileFriend.isPending && !profileFriend.isFriend && !profileFriend.isRequesting) {
                    userViewModel.sendFriendRequest {
                    }
                } else if (profileFriend.isPending) {
                    userViewModel.removeFriendRequest {
                    }
                } else if (profileFriend.isRequesting) {
                    userViewModel.acceptFriendRequest { }
                } else {
                    userViewModel.removeFriend {

                    }
                }
            }
        }

        // ** Populate sport cards
        //profile.populateSportCards(context, sportContainer)

    }

    private fun editProfile() {
        val homeActivity = activity as HomeActivity
        val intent = Intent(activity, EditProfileActivity::class.java)
        intent.putExtra("profile", profile.toJSON().toString())
        homeActivity.launcherEdit.launch(intent)
    }

    private fun returnFromFriendProfile() {
        val friendProfileActivity = activity as FriendProfileActivity
        friendProfileActivity.finish()
    }


    private fun resizeImageView(iv: ImageView) {

        val ll = requireView().findViewById<LinearLayout>(R.id.imageContainer)
        ll.post {
            val width = ll.width
            val height = ll.height
            if (width == height) return@post
            val diameter = width.coerceAtMost(height)
            iv.layoutParams = FrameLayout.LayoutParams(diameter, diameter)
            iv.requestLayout()
        }
    }

    private fun loadImage(imageUri: Uri?) {
        println("ImageURI $imageUri")
        val options: RequestOptions = RequestOptions()
        Glide.with(this)
            .load(imageUri)
            .signature(ObjectKey(imageUri.toString()))
            .apply(
                options.centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.person)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform()
            )
            .error(R.drawable.person)
            .into(requireView().findViewById(R.id.profile_image))
    }


}