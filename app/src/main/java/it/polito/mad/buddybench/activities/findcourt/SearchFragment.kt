package it.polito.mad.buddybench.activities.findcourt

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.view.WeekCalendarView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.court.CourtActivity
import it.polito.mad.buddybench.activities.court.ReviewsActivity
import it.polito.mad.buddybench.activities.court.WeeklyCalendarDayBinder
import it.polito.mad.buddybench.activities.findcourt.sportselection.SportsSelectionFragment
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.ImageViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.activity_search_court) {

    private lateinit var recyclerView: RecyclerView
    private var lastCourts: List<CourtDTO> = listOf()
    private lateinit var progressLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noCourts: TextView
    val imageViewModel by activityViewModels<ImageViewModel> ()
    val userViewModel by activityViewModels<UserViewModel> ()

    private lateinit var swipeRefresh : SwipeRefreshLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefresh = view.findViewById(R.id.swiperefresh)
        swipeRefresh.setOnRefreshListener {
            parent.viewModel.getCourtsBySport()
            swipeRefresh.isRefreshing = false
        }
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        val b = view.findViewById<ImageView>(R.id.change_sport_button)
        val textNearButton = view.findViewById<TextView>(R.id.textView12)
        val textUser = view.findViewById<TextView>(R.id.textView11)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val filterButton = view.findViewById<CardView>(R.id.filterButton)
        val filterIcon = view.findViewById<ImageView>(R.id.filterIcon)

        parent.context.userViewModel.user.observe(viewLifecycleOwner) {
            textUser.text = parent.context.getString(R.string.user_hello, it.name)
        }

        progressLayout = view.findViewById(R.id.progess_layout)
        progressBar = progressLayout.findViewById(R.id.progress_circular)

        noCourts = view.findViewById(R.id.no_courts_available)


        val callbackCourt: (String, Sports) -> Unit  = {
                name, sport ->
            val intent = Intent(context, CourtActivity::class.java)
            intent.putExtra("courtName", name)
            intent.putExtra("sport", sport.name.uppercase())
            intent.putExtra("date", parent.viewModel.getSelectedDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            parent.context.launcherReservation.launch(intent)
        }

        val reviewsCallback: (String, Sports) -> Unit = {
            name, sport ->
            run {
                val intent = Intent(context, ReviewsActivity::class.java)
                intent.putExtra("court_name", name)
                intent.putExtra("court_sport",sport.toString())
                parent.context.launcherReviews.launch(intent)
            }
        }

        recyclerView.adapter = CourtSearchAdapter(parent.viewModel.currentCourts, callbackCourt, reviewsCallback)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        val calendarView = view.findViewById<WeekCalendarView>(R.id.calendar)

        val calendarCallback: (LocalDate, LocalDate) -> Unit = { last, new ->
            if(last == new){
                calendarView.notifyDayChanged(WeekDay(last, WeekDayPosition.InDate))
            } else {
                calendarView.notifyDayChanged(WeekDay(new, WeekDayPosition.InDate))
                calendarView.notifyDayChanged(WeekDay(last, WeekDayPosition.InDate))
                parent.viewModel.setSelectedDate(new)
            }
        }

        calendarView.dayBinder = WeeklyCalendarDayBinder( parent.viewModel.getSelectedDate(), calendarCallback)
        val ranges = Utils.getDateRanges()
        calendarView.setup(ranges.first, ranges.second, DayOfWeek.MONDAY)
        calendarView.scrollToDate(parent.viewModel.getSelectedDate())

        filterButton.setOnClickListener{
            showBottomSheetDialog()
        }

        b.setOnClickListener{
            //parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
            val fragmentTransaction = parent.parentFragmentManager.beginTransaction()
            val sportsSelectionFragment = SportsSelectionFragment(parent)
            fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left)
            fragmentTransaction.replace(R.id.find_court, sportsSelectionFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            textUser.text = parent.context.getString(R.string.user_hello, userViewModel.user.value)
            // When I return to sport selection, clear filter
            parent.context.findCourtViewModel.clearFilters()
            filterButton?.setBackgroundResource(R.drawable.circle_light_bg)
            filterIcon?.setImageResource(R.drawable.filter)
        }

        parent.context.findCourtViewModel.loading.observe(viewLifecycleOwner) {
            if(it) {
                recyclerView.visibility = View.GONE
                noCourts.visibility = View.GONE
                progressLayout.visibility = View.VISIBLE
            } else {
                progressLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        parent.viewModel.currentCourts.observe(viewLifecycleOwner){
            val diff = CourtsDiffUtils(lastCourts, it)
            val diffResult = DiffUtil.calculateDiff(diff)
            lastCourts = it
            diffResult.dispatchUpdatesTo(recyclerView.adapter!!)
            recyclerView.scrollToPosition(0)
            parent.context.findCourtViewModel.loading.postValue(false)
            if(it.isEmpty()) {
                recyclerView.visibility = View.GONE
                noCourts.visibility = View.VISIBLE
                noCourts.text = emptyString()
            } else {
                recyclerView.visibility = View.VISIBLE
                noCourts.visibility = View.GONE
            }
        }

        parent.viewModel.selectedSport.observe(viewLifecycleOwner){
            val iconDrawable = ContextCompat.getDrawable(view.context,
                Sports.sportToIconDrawableAlternative(
                    it
                )
            )
            //** Sport selection button color
            b.backgroundTintList = ColorStateList.valueOf(Sports.getSportColor(parent.viewModel.selectedSport.value!!, requireContext()))
            val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
            wrappedDrawable.mutate().setTint(Color.WHITE)
            val bitmap = wrappedDrawable.toBitmap(160, 160)
            b.setImageBitmap(bitmap)

            textNearButton.text = getString(R.string.court_search_phrase, it.toString().lowercase().replaceFirstChar { c -> c.uppercase() })

            textUser.text = parent.context.getString(R.string.user_hello, userViewModel.user.value!!.name)
            parent.context.findViewById<ImageView>(R.id.close_selection).visibility = View.VISIBLE

            searchEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    parent.viewModel.name = s.toString().trim().replace("\\s+".toRegex(), " ")
                    parent.viewModel.applyFilter()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showBottomSheetDialog(){

        val filterButton = view?.findViewById<CardView>(R.id.filterButton)
        val filterIcon = view?.findViewById<ImageView>(R.id.filterIcon)

        val bottomSheet = FilterSheetDialog(
            parent.viewModel,
            filterButton!!,
            filterIcon!!
        )

        bottomSheet.show(parentFragmentManager, "filterSheet")

    }


    override  fun onStart() {
        super.onStart()
        parent.viewModel.getCourtsBySport()
    }

     private fun emptyString(): String{
        return if(parent.viewModel.filtersEnabled){
            "No courts found based on the search criteria"
        } else{
            "No courts available"
        }
    }

}