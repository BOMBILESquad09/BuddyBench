package it.polito.mad.buddybench.activities.court

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.persistence.dto.ReviewDTO

class ReviewsAdapter(private val data: LiveData<List<ReviewDTO>>): RecyclerView.Adapter<ReviewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
        return ReviewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.value!!.size
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val reviewItem = data.value!![position]
        holder.bind(reviewItem)
    }

}