package it.polito.mad.buddybench.activities.court

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.ReviewsDiffUtils
import it.polito.mad.buddybench.databinding.ActivityReviewsBinding
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.ReviewViewModel

@AndroidEntryPoint
class ReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewsBinding

    // ** Data
    private var courtName: String? = null
    private var courtSport: String? = null
    private var lastReviews: List<ReviewDTO> = listOf()
    lateinit var progressDialog: Dialog

    // ** View Models
    private val reviewViewModel by viewModels<ReviewViewModel>()
    val imageViewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)

        getArguments()
        setContentView(binding.root)
        uiSetup()
    }

    private fun getArguments() {
        courtName = intent.getStringExtra("court_name")
        courtSport = intent.getStringExtra("court_sport")
        progressDialog = Utils.openProgressDialog(this)
        reviewViewModel.getCourtReviews(courtName!!, courtSport!!, this)
    }

    // ** UI Elements binding
    private fun uiSetup() {
        binding.tvCourtNameReviews.text = courtName
        binding.backButtonReviews.setOnClickListener { finish() }

        // ** Edit button
        binding.btnEditReview.setOnClickListener { editReviewUI() }

        // ** New review card
        binding.rbNewReview.isClickable = true
        binding.btnNewReview.setOnClickListener { addReview() }

        // ** Recycler View
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = ReviewsAdapter(reviewViewModel.reviews)

        // ** Rating bars
        binding.rbYourReview.stepSize = 1F
        binding.rbNewReview.stepSize = 1F

        reviewViewModel.done.observe(this){
            if(it){
                progressDialog.dismiss()
            }
        }

        reviewViewModel.l.observe(this) {
            if (it) {
                binding.pbReviews.visibility = View.VISIBLE
                binding.rvReviews.visibility = View.GONE
                binding.tvNoReviews.visibility = View.GONE
            } else {
                binding.pbReviews.visibility = View.GONE
                binding.rvReviews.visibility = View.VISIBLE
            }
        }

        // ** Update recycler view
        reviewViewModel.reviews.observe(this){
            val diff = ReviewsDiffUtils(lastReviews, it)
            val diffResult = DiffUtil.calculateDiff(diff)
            lastReviews = it
            diffResult.dispatchUpdatesTo(binding.rvReviews.adapter!!)
            binding.rvReviews.scrollToPosition(0)
            if(it.isEmpty()) {
                binding.rvReviews.visibility = View.GONE
                binding.tvNoReviews.visibility = View.VISIBLE
            } else {
                binding.rvReviews.visibility = View.VISIBLE
                binding.tvNoReviews.visibility = View.GONE
            }
        }

        // ** User can review
        reviewViewModel.canReview.observe(this) { can ->
            if (can) {
                binding.cardNewReview.visibility = View.VISIBLE
                binding.cardYourReview.visibility = View.GONE
                binding.tvCannotReview.visibility = View.GONE
            } else {
                binding.cardNewReview.visibility = View.GONE
                binding.cardYourReview.visibility = View.GONE
                binding.tvCannotReview.visibility = View.VISIBLE
            }
        }

        // ** User review
        reviewViewModel.userReview.observe(this) {
            if (it != null) {
                binding.tvYourReview.text = it.description
                binding.rbYourReview.invalidate()
                binding.rbYourReview.setIsIndicator(false)
                binding.rbYourReview.rating = it.rating.toFloat()
                binding.rbYourReview.setIsIndicator(true)

                binding.cardNewReview.visibility = View.GONE
                binding.cardYourReview.visibility = View.VISIBLE
                binding.tvCannotReview.visibility = View.GONE
            }
        }

        // ** Update court data
        reviewViewModel.court.observe(this) {
            binding.tvReviewTotal.text = Utils.roundOffDecimal(it.rating).toString()
            binding.tvNumReviews.text = String.format(getString(R.string.num_ratings), it.nReviews)
            binding.rbReviewTotal.rating = it.rating.toFloat()
        }
    }

    private fun addReview() {
        if (binding.rbNewReview.rating.equals(0.0f)) {
            binding.tvErrorReview.visibility = View.VISIBLE
            return
        }

        binding.tvErrorReview.visibility = View.GONE
        reviewViewModel.insertReview(binding.etNewReview.text.toString(), binding.rbNewReview.rating.toInt(), this)
    }

    private fun editReviewUI() {
        val userReviewData = reviewViewModel.userReview.value!!
        binding.etNewReview.text = SpannableStringBuilder(userReviewData.description)
        binding.rbNewReview.rating = userReviewData.rating.toFloat()

        binding.cardYourReview.visibility = View.GONE
        binding.cardNewReview.visibility = View.VISIBLE
    }
}