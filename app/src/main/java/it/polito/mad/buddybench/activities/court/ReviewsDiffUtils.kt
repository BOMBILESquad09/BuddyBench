package it.polito.mad.buddybench.activities.findcourt

import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO

class ReviewsDiffUtils(
    private val lastReviews: List<ReviewDTO>,
    private val newReviews: List<ReviewDTO>
): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastReviews.size
    }

    override fun getNewListSize(): Int {
        return newReviews.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastReviews[oldItemPosition].user == newReviews[newItemPosition].user &&
                lastReviews[oldItemPosition].courtDTO == newReviews[newItemPosition].courtDTO
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastReviews[oldItemPosition].courtDTO == newReviews[newItemPosition].courtDTO &&
                lastReviews[oldItemPosition].user == newReviews[newItemPosition].user
    }

}