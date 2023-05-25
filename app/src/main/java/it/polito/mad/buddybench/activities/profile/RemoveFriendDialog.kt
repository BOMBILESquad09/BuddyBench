package it.polito.mad.buddybench.activities.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.viewmodels.FindCourtViewModel
import it.polito.mad.buddybench.viewmodels.FriendsViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel

@AndroidEntryPoint
class RemoveFriendDialog(
    private val userViewModel: UserViewModel,
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_remove_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val confirmText = view.findViewById<TextView>(R.id.confirm_text)

        confirmText.text = "Do you want to remove " + userViewModel.user.value?.name + " from your friend list?"

        val confirmButton = view.findViewById<Button>(R.id.btn_confirm)
        confirmButton?.setOnClickListener{
            userViewModel.removeFriend {
                dismiss()
            }
        }

        val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
        cancelButton?.setOnClickListener{
            dismiss()
        }

    }




}