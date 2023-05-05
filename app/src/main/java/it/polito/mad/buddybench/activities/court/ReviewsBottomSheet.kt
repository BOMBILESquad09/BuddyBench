package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReviewViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_COURT_NAME = "court_name"
private const val ARG_COURT_SPORT = "court_sport"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewsBottomSheet.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewsBottomSheet : SuperBottomSheetFragment() {

    // ** UI Elements
    private lateinit var parent: View
    private lateinit var tvCourtName: TextView
    private lateinit var rvReviews: RecyclerView
    private lateinit var backButton: ImageButton

    // ** Data
    private var courtName: String? = null
    private var courtSport: String? = null
    private lateinit var court: CourtDTO

    // ** View Models
    private val courtViewModel = viewModels<CourtViewModel>()
    private val reviewViewModel = viewModels<ReviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courtName = it.getString(ARG_COURT_NAME)
            courtSport = it.getString(ARG_COURT_SPORT)
        }
        // ** Fetch court (?) and reviews while loading
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.parent = view
        uiSetup()
    }

    // ** UI Elements binding
    private fun uiSetup() {
        tvCourtName = parent.findViewById(R.id.tv_court_name_reviews)
        tvCourtName.text = courtName

        backButton = parent.findViewById(R.id.back_button_reviews)
        backButton.setOnClickListener { this.dismiss() }
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