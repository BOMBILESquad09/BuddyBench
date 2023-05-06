package it.polito.mad.buddybench.activities.findcourt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel

@AndroidEntryPoint
class FilterSheetDialog(
    val findCourtViewModel: FindCourtViewModel,
    val refClearButton: CardView,
    val refFilterIcon: ImageView
): SuperBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_dialog_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val minRating: Float = findCourtViewModel.minRating
        val maxFee: Float = findCourtViewModel.maxFee

        val rangeSliderPrice : RangeSlider = view.findViewById<RangeSlider>(R.id.range_slider_price)
        rangeSliderPrice.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSliderPrice.setValues(maxFee)
        rangeSliderPrice.stepSize = 1f

        val rangeSliderRating : RangeSlider = view.findViewById<RangeSlider>(R.id.range_slider_rating)
        rangeSliderRating.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSliderRating.setValues(minRating)
        rangeSliderRating.stepSize = 1f

        val showMinRating = view.findViewById<TextView>(R.id.minRating)
        val showMaxPrice = view.findViewById<TextView>(R.id.maxFee)
        showMinRating.text = minRating.toInt().toString()
        showMaxPrice.text = maxFee.toInt().toString() + "€"

        rangeSliderRating.addOnChangeListener(RangeSlider.OnChangeListener { _, value, _ -> showMinRating?.text = value.toInt().toString() })

        rangeSliderPrice.addOnChangeListener(RangeSlider.OnChangeListener { _, value, _ -> showMaxPrice?.text = value.toInt().toString() + "€" })

        val confirmButton = view.findViewById<Button>(R.id.confirmFilter)
        confirmButton?.setOnClickListener{
            findCourtViewModel.minRating = rangeSliderRating.values.get(0)!!
            findCourtViewModel.maxFee = rangeSliderPrice.values.get(0)!!

            findCourtViewModel.applyFilter()
            refClearButton.setBackgroundResource(R.drawable.circle_dark_bg)
            refFilterIcon.setImageResource(R.drawable.filter_white)
            dismiss()
        }

        val clearButton = view.findViewById<Button>(R.id.clearFilter)
        clearButton?.setOnClickListener{
            findCourtViewModel.clearFilters()
            findCourtViewModel.applyFilter()
            refClearButton.setBackgroundResource(R.drawable.circle_light_bg)
            refFilterIcon.setImageResource(R.drawable.filter)
            dismiss()
        }

    }

    override fun isSheetAlwaysExpanded(): Boolean {
        return true
    }

    override fun getExpandedHeight(): Int {
        return -2
    }


}