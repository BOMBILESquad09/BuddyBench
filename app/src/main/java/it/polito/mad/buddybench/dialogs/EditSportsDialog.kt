package it.polito.mad.buddybench.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.compose.ui.unit.dp
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils

class EditSportsDialog(private val profile: Profile,var selectedItems: ArrayList<Sports>): DialogFragment() {

    // Use this instance of the interface to deliver action events
    private lateinit var listener: NoticeDialogListener
    private lateinit var dialog: Dialog

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, selectedItems: ArrayList<Sports>)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {

            val profileSports: List<Sports> = profile.getSportsEnum()
            val sportValues = Sports.getStringValues(profileSports)

            if (sportValues.isEmpty()) {
                dialog = Utils.openGeneralProblemDialog(
                    getString(R.string.edit_sports_error_title),
                    getString(R.string.edit_sports_error),
                    it
                )
            } else {
                // Use the Builder class for convenient dialog construction
                val dialogCard = layoutInflater.inflate(R.layout.dialog_edit_sports, null)
                val confirm = dialogCard.findViewById<View>(R.id.confirm)
                confirm.isFocusable = false
                confirm.isClickable = false
                val radioGroup = dialogCard.findViewById<RadioGroup>(R.id.visibility_group)
                radioGroup.removeAllViews()
                sportValues.forEachIndexed { idx, sport ->
                    val checkbox = MaterialCheckBox(it)
                    checkbox.id = idx
                    checkbox.setPadding(10)
                    checkbox.text = sport
                    checkbox.textSize = 20f
                    radioGroup.addView(
                        checkbox
                    )
                    checkbox.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(Sports.fromJSON(sportValues[idx] as String)!!)
                        } else if (selectedItems.contains(Sports.fromJSON(sportValues[idx] as String))) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Sports.fromJSON(sportValues[idx] as String))
                        }
                    }
                }

                val builder = AlertDialog.Builder(it)
                builder.setView(dialogCard)
                dialog = builder.create()
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.show()
                dialogCard.findViewById<View>(R.id.cancel).setOnClickListener {
                    listener.onDialogNegativeClick(this)
                }
                dialogCard.findViewById<View>(R.id.confirm).setOnClickListener {
                    listener.onDialogPositiveClick(this, selectedItems)
                }
            }

            // Create dialog
            dialog

        } ?: throw java.lang.IllegalStateException()
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }



}