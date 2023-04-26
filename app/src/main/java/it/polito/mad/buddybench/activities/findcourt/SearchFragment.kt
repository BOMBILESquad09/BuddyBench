package it.polito.mad.buddybench.activities.findcourt

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.findcourt.sportselection.CourtSearchAdapterAdapter
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.viewmodels.CourtViewModel

@AndroidEntryPoint
class SearchFragment(val parent: FindCourtFragment): Fragment(R.layout.activity_search_court) {

    private val courtViewModel by viewModels<CourtViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
        val b = view.findViewById<ImageButton>(R.id.change_sport_button)
        val textNearButton = view.findViewById<TextView>(R.id.textView12)
        val textUser = view.findViewById<TextView>(R.id.textView11)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val filterButton = view.findViewById<CardView>(R.id.filterButton)

        textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)

        b.setOnClickListener{
            parent.fragmentManager.switchFragment(States.SPORTS_SELECTION)
            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)
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

            val l = courtViewModel.getCourtsBySport(it)
            recyclerView?.adapter = CourtSearchAdapterAdapter(l)
            recyclerView?.layoutManager = LinearLayoutManager(view.context)
            textUser.text = parent.context.getString(R.string.user_hello, parent.context.profile.name)

            searchEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val filteredData = l.filter {
                        it.location.contains(s.toString(), ignoreCase = true) || it.name.contains(s.toString(), ignoreCase = true)
                    }
                    recyclerView.adapter = CourtSearchAdapterAdapter(filteredData)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        filterButton.setOnClickListener{showBottomSheetDialog()}

        super.onViewCreated(view, savedInstanceState)
    }

    private fun showBottomSheetDialog(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val rangeSliderPrice = bottomSheetDialog.findViewById<RangeSlider>(R.id.range_slider_price)
        rangeSliderPrice?.setValues(2.0f,75.0f)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_filter)
        bottomSheetDialog.show()
    }





}