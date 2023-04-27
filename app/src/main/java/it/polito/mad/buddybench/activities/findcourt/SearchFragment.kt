package it.polito.mad.buddybench.activities.findcourt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.view.WeekCalendarView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.court.CourtActivity
import it.polito.mad.buddybench.activities.court.WeeklyCalendarDayBinder
import it.polito.mad.buddybench.activities.findcourt.sportselection.CourtSearchAdapter
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.activity_search_court) {

    lateinit var recyclerView: RecyclerView
    private var lastCourts: List<CourtDTO> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.searchRecyclerView)
        val b = view.findViewById<ImageButton>(R.id.change_sport_button)
        val textNearButton = view.findViewById<TextView>(R.id.textView12)
        val textUser = view.findViewById<TextView>(R.id.textView11)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val filterButton = view.findViewById<CardView>(R.id.filterButton)



        textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)

        val callbackCourt: (String, Sports) -> Unit  = {
                name, sport ->
            val intent = Intent(context, CourtActivity::class.java)
            intent.putExtra("courtName", name)
            intent.putExtra("sport", sport.name.uppercase())
            intent.putExtra("date", parent.viewModel.getSelectedDate().format(DateTimeFormatter.ISO_LOCAL_DATE))

            context?.startActivity(intent)
        }

        recyclerView.adapter = CourtSearchAdapter(parent.viewModel.currentCourts, callbackCourt)
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
            parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)
        }

        parent.viewModel.currentCourts.observe(viewLifecycleOwner){
            val diff = CourtsDiffUtils(lastCourts, it)
            val diffResult = DiffUtil.calculateDiff(diff)
            lastCourts = it
            diffResult.dispatchUpdatesTo(recyclerView.adapter!!)
            recyclerView.scrollToPosition(0)

        }

        parent.viewModel.selectedSport.observe(viewLifecycleOwner){
            val iconDrawable = ContextCompat.getDrawable(view.context,
                Sports.sportToIconDrawable(
                    it
                )
            )
            val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
            wrappedDrawable.mutate().setTint(Color.WHITE)
            val bitmap = wrappedDrawable.toBitmap(160, 160)
            b.setImageBitmap(bitmap)

            textNearButton.text = getString(R.string.court_search_phrase, it.toString().lowercase().replaceFirstChar { c -> c.uppercase() })
            parent.viewModel.getCourtsBySport(it, parent.viewModel.getSelectedDate())

            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)
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
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_filter)

        val rangeSliderPrice : RangeSlider? = bottomSheetDialog.findViewById<RangeSlider>(R.id.range_slider_price)
        rangeSliderPrice?.setValues(0f,100f)
        rangeSliderPrice?.stepSize = 1f

        val rangeSliderRating : RangeSlider? = bottomSheetDialog.findViewById<RangeSlider>(R.id.range_slider_rating)
        rangeSliderRating?.setValues(0f,5f)
        rangeSliderRating?.stepSize = 1f

        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirmFilter)
        bottomSheetDialog.show()
    }





}