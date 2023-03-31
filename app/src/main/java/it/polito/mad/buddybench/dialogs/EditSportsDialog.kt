package it.polito.mad.buddybench.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports

class EditSportsDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Selected Sports
            val selectedItems = ArrayList<Sports?>()
            val sharedPref: SharedPreferences = it.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.edit_your_sports)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.sports, null
                ) { dialog, which, isChecked ->

                    println("${Sports.fromIntToEnum(which)}")

                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        selectedItems.add(Sports.fromIntToEnum(which))
                    } else if (selectedItems.contains(Sports.fromIntToEnum(which))) {
                        // Else, if the item is already in the array, remove it
                        selectedItems.remove(Sports.fromIntToEnum(which))
                    }
                }
                // Set the action buttons
                .setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        println("Save")
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        println("Cancel")
                    })


            // Create dialog
            builder.create()
        } ?: throw java.lang.IllegalStateException()
    }
}

/*
val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_sport_selection, null))
                .setPositiveButton("Save", DialogInterface.OnClickListener {
                    dialogInterface, id -> println("Dialog save clicked")
                }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, id -> println("Cancel clicked") })
 */