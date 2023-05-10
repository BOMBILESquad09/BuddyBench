package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.ReviewsDiffUtils
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReviewDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ReviewViewModel

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURT_NAME = "court_name"
private const val ARG_COURT_SPORT = "court_sport"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewsBottomSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ReviewsBottomSheet : SuperBottomSheetFragment() {

    // ** UI Elements
    private lateinit var parent: View

    // ** TextViews
    private lateinit var tvCourtName: TextView
    private lateinit var tvNoReviews: TextView
    private lateinit var tvCannotReview: TextView
    private lateinit var tvYourReview: TextView
    private lateinit var tvNumReviews: TextView
    private lateinit var tvReviewTotal: TextView
    private lateinit var tvErrorReview: TextView

    // ** Buttons
    private lateinit var btnNewReview: Button
    private lateinit var btnEditReview: ImageButton
    private lateinit var backButton: ImageButton

    // ** Cards
    private lateinit var cardNewReview: CardView
    private lateinit var cardYourReview: CardView

    // ** Rating bars
    private lateinit var rbReviewTotal: RatingBar
    private lateinit var rbYourReview: RatingBar
    private lateinit var rbNewReview: RatingBar

    // ** Other
    private lateinit var etNewReview: EditText
    private lateinit var rvReviews: RecyclerView
    private lateinit var pbReviews: ProgressBar

    // ** Data
    private var courtName: String? = null
    private var courtSport: String? = null
    private var lastReviews: List<ReviewDTO> = listOf()
    private lateinit var court: CourtDTO
    private lateinit var userReviewData: ReviewDTO

    // ** View Models
    private val reviewViewModel by viewModels<ReviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courtName = it.getString(ARG_COURT_NAME)
            courtSport = it.getString(ARG_COURT_SPORT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ** UI
        parent = view
        uiSetup()
    }

    // ** UI Elements binding
    private fun uiSetup() {
        tvCourtName = parent.findViewById(R.id.tv_court_name_reviews)
        tvCourtName.text = courtName

        backButton = parent.findViewById(R.id.back_button_reviews)
        backButton.setOnClickListener { this.dismiss() }

        // ** Review Total
        tvReviewTotal = parent.findViewById(R.id.tv_review_total)
        tvNumReviews = parent.findViewById(R.id.tv_num_reviews)
        rbReviewTotal = parent.findViewById(R.id.rb_review_total)

        // ** Cannot Review
        tvCannotReview = parent.findViewById(R.id.tv_cannot_review)

        // ** Your review card
        cardYourReview = parent.findViewById(R.id.card_your_review)
        btnEditReview = parent.findViewById(R.id.btn_edit_review)
        tvYourReview = parent.findViewById(R.id.tv_your_review)
        rbYourReview = parent.findViewById(R.id.rb_your_review)
        btnEditReview.setOnClickListener { editReview() }


        // ** New review card
        cardNewReview = parent.findViewById(R.id.card_new_review)
        btnNewReview = parent.findViewById(R.id.btn_new_review)
        etNewReview = parent.findViewById(R.id.et_new_review)
        rbNewReview = parent.findViewById(R.id.rb_new_review)
        rbNewReview.isClickable = true
        tvErrorReview = parent.findViewById(R.id.tv_error_review)

        btnNewReview.setOnClickListener { addReview() }

        // ** Recycler View
        rvReviews = parent.findViewById(R.id.rv_reviews)
        rvReviews.layoutManager = LinearLayoutManager(context)
        rvReviews.adapter = ReviewsAdapter(reviewViewModel.reviews)

        // ** Empty data
        tvNoReviews = parent.findViewById(R.id.tv_no_reviews)

        // ** Loading state
        pbReviews = parent.findViewById(R.id.pb_reviews)
        reviewViewModel.l.observe(this) {
            if (it) {
                pbReviews.visibility = View.VISIBLE
                rvReviews.visibility = View.GONE
                tvNoReviews.visibility = View.GONE
            } else {
                pbReviews.visibility = View.GONE
                rvReviews.visibility = View.VISIBLE
            }
        }

        // ** Update recycler view
        reviewViewModel.reviews.observe(this){
            val diff = ReviewsDiffUtils(lastReviews, it)
            val diffResult = DiffUtil.calculateDiff(diff)
            lastReviews = it
            diffResult.dispatchUpdatesTo(rvReviews.adapter!!)
            rvReviews.scrollToPosition(0)
            if(it.isEmpty()) {
                rvReviews.visibility = View.GONE
                tvNoReviews.visibility = View.VISIBLE
            } else {
                rvReviews.visibility = View.VISIBLE
                tvNoReviews.visibility = View.GONE
            }
        }

        // ** Update court data
        reviewViewModel.court.observe(this) {
            court = it
            tvReviewTotal.text = Utils.roundOffDecimal(it.rating).toString()
            tvNumReviews.text = String.format(getString(R.string.num_ratings), it.nReviews)
            rbReviewTotal.rating = it.rating.toFloat()
        }

        // ** User review card
        reviewViewModel.userReview.observe(this) { userReview ->
            if (userReview !== null) {
                userReviewData = userReview

                cardNewReview.visibility = View.GONE
                cardYourReview.visibility = View.VISIBLE
                tvCannotReview.visibility = View.GONE

                tvYourReview.text = userReview.description
                rbYourReview.rating = userReview.rating.toFloat()
            }

            reviewViewModel.canReview.observe(this) { can ->
                if (userReview !== null) {
                    cardNewReview.visibility = View.GONE
                    cardYourReview.visibility = View.VISIBLE
                    tvCannotReview.visibility = View.GONE

                    tvYourReview.text = userReview.description
                    rbYourReview.rating = userReview.rating.toFloat()
                } else {
                    if (can) {
                        cardNewReview.visibility = View.VISIBLE
                        cardYourReview.visibility = View.GONE
                        tvCannotReview.visibility = View.GONE
                    } else {
                        cardNewReview.visibility = View.GONE
                        cardYourReview.visibility = View.GONE
                        tvCannotReview.visibility = View.VISIBLE
                    }
                }
            }
        }

        reviewViewModel.getCourtReviews(courtName!!, courtSport!!, requireContext())
        reviewViewModel.getCourt(courtName!!, courtSport!!)
        reviewViewModel.userCanReview(courtName!!, courtSport!!, requireContext())
        reviewViewModel.getUserReview(courtName!!, courtSport!!, requireContext())
    }


    private fun addReview() {
        if (rbNewReview.rating.equals(0.0f)) {
            tvErrorReview.visibility = View.VISIBLE
            return
        }

        tvErrorReview.visibility = View.GONE
        reviewViewModel.insertReview(court, etNewReview.text.toString(), rbNewReview.rating.toInt(), requireContext())
        reviewViewModel.getUserReview(courtName!!, courtSport!!, requireContext())
    }

    private fun editReview() {
        etNewReview.text = SpannableStringBuilder(userReviewData.description)
        rbNewReview.rating = userReviewData.rating.toFloat()

        cardYourReview.visibility = View.GONE
        cardNewReview.visibility = View.VISIBLE
    }

    // ** Bottom Sheet is always expanded
    override fun isSheetAlwaysExpanded(): Boolean {
        return true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param court_name Court name.
         * @param court_sport Court sport.
         * @return A new instance of fragment ReviewsBottomSheet.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(court_name: String, court_sport: String) =
            ReviewsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURT_NAME, court_name)
                    putString(ARG_COURT_SPORT, court_sport)
                }
            }

    }
}