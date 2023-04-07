package it.polito.mad.buddybench.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils

class EditSportsDialog(private val profile: Profile,var selectedItems: ArrayList<Sports>): DialogFragment() {

    // Use this instance of the interface to deliver action events
    private lateinit var listener: NoticeDialogListener

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

            // Selected Sports
            val alreadySelected = sportValues.map { s -> selectedItems.contains(Sports.fromJSON(s.toString())) }.toBooleanArray()
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.add_sports)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(sportValues, alreadySelected
                ) { dialog, which, isChecked ->


                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        selectedItems.add(Sports.fromJSON(sportValues[which] as String)!!)
                    } else if (selectedItems.contains(Sports.fromJSON(sportValues[which] as String))) {
                        // Else, if the item is already in the array, remove it
                        selectedItems.remove(Sports.fromJSON(sportValues[which] as String))
                    }

                }
                // Set the action buttons
                .setPositiveButton(R.string.save) { dialog, id ->
                    // User clicked OK, so save the selectedItems results somewhere
                    // or return them to the component that opened the dialog
                    listener.onDialogPositiveClick(this, selectedItems)
                }
                .setNegativeButton("cancel") { dialog, id ->
                    listener.onDialogNegativeClick(this)


                }



            // Create dialog
            builder.create()
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