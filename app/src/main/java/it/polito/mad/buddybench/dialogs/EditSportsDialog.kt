package it.polito.mad.buddybench.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import it.polito.mad.buddybench.R

class EditSportsDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            // Apply custom layout
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_sport_selection, null))
                .setPositiveButton("Save", DialogInterface.OnClickListener {
                    dialogInterface, id -> println("Dialog save clicked")
                }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, id -> println("Cancel clicked") })

            // Create dialog
            builder.create()
        }
    }
}