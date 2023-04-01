package it.polito.mad.buddybench.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports
import org.json.JSONObject

class EditSportsDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val profile = Profile.getProfileFromSharedPreferences(it)
            val profileSports: List<Sports> = profile.getSportsEnum()
            println("Profile Sports To Exclude: $profileSports")
            val sportValues = Sports.getStringValues(profileSports)
            println("Sport Values: ${sportValues}")

            // Selected Sports
            val selectedItems = ArrayList<Sports?>()

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.add_sports)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(sportValues, null
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