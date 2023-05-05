package it.polito.mad.buddybench.activities.court

import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.persistence.dto.ReviewDTO

class ReviewsViewHolder(private val v: View): RecyclerView.ViewHolder(v) {

    // ** UI Elements
    private lateinit var tvUsername: TextView
    private lateinit var ivUserPicture: ImageView
    private lateinit var tvRatingValue: TextView
    private lateinit var rbRatingBar: RatingBar
    private lateinit var tvDescription: TextView

    // ** Bind
    fun bind(review: ReviewDTO) {
        // ** Username
        tvUsername = v.findViewById(R.id.tv_username_review)
        tvUsername.text = review.user.name

        // ** User profile picture
        // TODO: Add user profile picture
        ivUserPicture = v.findViewById(R.id.iv_user_picture_review)

        // ** Rating
        tvRatingValue = v.findViewById(R.id.tv_rating_value)
        tvRatingValue.text = review.rating.toString()
        rbRatingBar = v.findViewById(R.id.rating_bar_value_review)
        rbRatingBar.numStars = review.rating
        rbRatingBar.isClickable = false

        // ** Description
        tvDescription = v.findViewById(R.id.tv_review_description)
        tvDescription.text = review.description
    }
}