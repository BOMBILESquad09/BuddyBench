package it.polito.mad.buddybench.activities.court

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.internal.managers.FragmentComponentManager
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.utils.Utils
import java.time.format.DateTimeFormatter

class ReviewsViewHolder(private val v: View): RecyclerView.ViewHolder(v) {

    // ** UI Elements
    private lateinit var tvUsername: TextView
    private lateinit var ivUserPicture: ImageView
    private lateinit var tvRatingValue: TextView
    private lateinit var rbRatingBar: RatingBar
    private lateinit var tvDescription: TextView
    private lateinit var tvReviewDate: TextView

    // ** Bind
    fun bind(review: ReviewDTO) {
        // ** Username
        tvUsername = v.findViewById(R.id.tv_username_review)
        tvUsername.text = review.user.nickname

        // ** Date
        tvReviewDate = v.findViewById(R.id.tv_review_date)
        tvReviewDate.text = review.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))

        // ** User profile picture
        // TODO: Add user profile picture
        ivUserPicture = v.findViewById(R.id.iv_user_picture_review)
        (FragmentComponentManager.findActivity(v.context) as ReviewsActivity).imageViewModel.getUserImage(review.user.email,{
            ivUserPicture.setImageResource(R.drawable.person)
        }){
            Glide.with(v.context)
                .load(it)
                .into(ivUserPicture)
        }
        ivUserPicture.setOnClickListener {
            Utils.goToProfileFriend(
                FragmentComponentManager.findActivity(v.context) as AppCompatActivity,
                review.user
            )
        }
        // ** Rating
        tvRatingValue = v.findViewById(R.id.tv_rating_value)
        tvRatingValue.text = review.rating.toString()
        rbRatingBar = v.findViewById(R.id.rating_bar_value_review)
        rbRatingBar.rating = review.rating.toFloat()
        rbRatingBar.isClickable = false

        // ** Description
        tvDescription = v.findViewById(R.id.tv_review_description)
        tvDescription.text = review.description
    }
}