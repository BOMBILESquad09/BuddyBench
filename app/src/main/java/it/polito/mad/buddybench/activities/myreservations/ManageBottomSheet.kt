package it.polito.mad.buddybench.activities.myreservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity
import it.polito.mad.buddybench.utils.Utils

class ManageBottomSheet(
    val onEditSelected: () -> Unit,
    val onInviteFriendsSelected: () -> Unit,
    val onChangeVisibility: () -> Unit

): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view  = inflater.inflate(R.layout.manage_bottom_sheet, container, false)
        val behaviour = BottomSheetBehavior.from(view.findViewById(R.id.standard_bottom_sheet))
        behaviour.state = STATE_EXPANDED
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inviteFriendsBtn = view.findViewById<LinearLayout>(R.id.invite_button)
        inviteFriendsBtn?.setOnClickListener {
            onInviteFriendsSelected()
            this.dismiss()
        }
        val editReservationBtn = view.findViewById<LinearLayout>(R.id.edit_reservation)
        editReservationBtn?.setOnClickListener {
            Utils.openProgressDialog(view.context)
            onEditSelected()
            this.dismiss()
        }



        val changeVisibilityButton = view.findViewById<LinearLayout>(R.id.change_visibility)
        changeVisibilityButton?.setOnClickListener {
            onChangeVisibility()
            this.dismiss()
        }


    }

}