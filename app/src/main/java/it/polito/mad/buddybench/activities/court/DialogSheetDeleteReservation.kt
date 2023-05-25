package it.polito.mad.buddybench.activities.court

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.apachat.loadingbutton.core.customViews.CircularProgressButton
import com.apachat.loadingbutton.core.presentation.State
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class DialogSheetDeleteReservation(
    private val reservationDTO: ReservationDTO,
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
        val button = view.findViewById<CircularProgressButton>(R.id.confirm_cancel)
        button.setOnClickListener {
            this.isCancelable = false

            button.startAnimation()

            reservationViewModel.deleteReservation(reservationDTO, oldDate, {}) {
                button.postOnAnimation {
                    Thread {
                        Thread.sleep(1000)
                        callback()
                    }.start()
                }
                val bitmap =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.done_foreground
                    )!!
                        .toBitmap()
                button.doneLoadingAnimation(0, bitmap)

            }
        }

    }
}


