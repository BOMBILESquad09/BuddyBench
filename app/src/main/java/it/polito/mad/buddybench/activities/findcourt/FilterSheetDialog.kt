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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel

@AndroidEntryPoint
class FilterSheetDialog(
    val findCourtViewModel: FindCourtViewModel,
    val refClearButton: CardView,
    val refFilterIcon: ImageView
): BottomSheetDialogFragment() {

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

        val rangeSliderPrice : Slider = view.findViewById<Slider>(R.id.range_slider_price)
        rangeSliderPrice.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSliderPrice.value = maxFee

        val rangeSliderRating : Slider = view.findViewById<Slider>(R.id.range_slider_rating)
        rangeSliderRating.labelBehavior = LabelFormatter.LABEL_GONE
        rangeSliderRating.value = minRating

        val showMinRating = view.findViewById<TextView>(R.id.minRating)
        val showMaxPrice = view.findViewById<TextView>(R.id.maxFee)
        showMinRating.text = minRating.toInt().toString()
        showMaxPrice.text = maxFee.toInt().toString() + "€"

        rangeSliderRating.addOnChangeListener(Slider.OnChangeListener { _, value, _ -> showMinRating?.text = value.toInt().toString() })

        rangeSliderPrice.addOnChangeListener(Slider.OnChangeListener { _, value, _ -> showMaxPrice?.text = value.toInt().toString() + "€" })

        val confirmButton = view.findViewById<MaterialButton>(R.id.confirmFilter)
        confirmButton?.setOnClickListener{
            findCourtViewModel.minRating = rangeSliderRating.value
            findCourtViewModel.maxFee = rangeSliderPrice.value

            // TODO: Insert an if to set filter for them
            findCourtViewModel.applyFilterOnCourts()
            refClearButton.setBackgroundResource(R.drawable.circle_dark_bg)
            refFilterIcon.setImageResource(R.drawable.filter_white)
            dismiss()
        }

        val clearButton = view.findViewById<MaterialButton>(R.id.clearFilter)
        clearButton?.setOnClickListener{
            // TODO: Insert an if to set filter for them
            findCourtViewModel.applyFilterOnCourts(clear = true)
            refClearButton.setBackgroundResource(R.drawable.circle_light_bg)
            refFilterIcon.setImageResource(R.drawable.filter)
            dismiss()
        }

    }




}