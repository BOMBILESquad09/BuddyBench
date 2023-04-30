package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class DialogSheetDeleteReservation(
    private val courtName: String,
    private val sport: Sports,
    private val oldStartTime: LocalTime,
    private val oldDate: LocalDate,
    private val email: String
) : SuperBottomSheetFragment() {

    private val reservationViewModel by viewModels<ReservationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheed_delete_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.confirm_cancel)
        button.setOnClickListener {
            reservationViewModel.deleteReservation(
                courtName,
                sport,
                oldStartTime,
                oldDate,
                email
            )
            dismiss()
            requireActivity().finish()
        }
    }
}