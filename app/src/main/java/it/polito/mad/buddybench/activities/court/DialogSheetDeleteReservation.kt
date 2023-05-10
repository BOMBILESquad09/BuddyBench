package it.polito.mad.buddybench.activities.court

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.kusu.library.LoadingButton
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
    private val email: String,
    private val callback: () -> Unit
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
        val button = view.findViewById<LoadingButton>(R.id.confirm_cancel)
        button.setOnClickListener {
            reservationViewModel.deleteReservation(
                button,
                courtName,
                sport,
                oldStartTime,
                oldDate,
                email,
                this,
                callback
            )
        }
    }


}