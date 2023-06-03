package it.polito.mad.buddybench.activities.invitations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel

@AndroidEntryPoint
class CancelRequestJoinDialog(
): BottomSheetDialogFragment() {
    private val reservationViewModel by activityViewModels<ReservationViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_cancel_participation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)
        confirmButton?.setOnClickListener{
            //TODO
            dismiss()
        }

        val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
        cancelButton?.setOnClickListener{
            dismiss()
        }

    }




}