package it.polito.mad.buddybench.activities.court

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kusu.library.LoadingButton
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EditConfirmDialogSheet(
    private val editMode: Boolean,
    private val courtViewModel: CourtViewModel,
    private val courtToReserve: CourtDTO,
    private val equipment: Boolean?,
    private val reservationViewModel: ReservationViewModel,
    private val user: UserDTO,
    private val selectedDate: LocalDate,
    private var oldDate: LocalDate?,
    private val oldStartTime: LocalTime?,
    private val callback: () -> Unit
) : SuperBottomSheetFragment() {

    private lateinit var switch: Switch
    private lateinit var sheet: NestedScrollView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_dialog_court_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Take the reference for the switch of equipment
        switch = view.findViewById(R.id.switch_equipment)
        sheet = view.findViewById(R.id.bottom_sheet_scroll_view)

        // Setting up the three card
        // Setting the values for the first Card in dialog
        setFirstCard(view)
        // Setting up the Additional Information Card
        setAdditionalInformationCard(view)
        // Setting the price details Dialog
        setPriceDetailCard(view)

        // CheckBox inside the Additional Information Card
        val checkboxAccept = view.findViewById<CheckBox>(R.id.accept_checkbox)
        // Take the reference of the confirm button
        val confirmButton = view.findViewById<LoadingButton>(R.id.confirmPrenotation)
        confirmButton?.setOnClickListener {
            if (!checkboxAccept!!.isChecked) {
                val textError = String.format(getString(R.string.error_info), courtToReserve.name)
                buildAlertDialog(
                    "Additional Information",
                    textError,
                    view.context
                ).show()
            } else {
                val reservation = ReservationDTO(
                    userOrganizer = user,
                    court = courtToReserve,
                    date = selectedDate,
                    startTime = courtViewModel.selectedTimes.first(),
                    endTime = courtViewModel.selectedTimes.last().plusHours(1),
                    equipment = switch.isChecked
                )

                this.isCancelable = false
                switch.isEnabled = false
                checkboxAccept.isEnabled = false


                confirmButton.showLoading()
                reservationViewModel.saveReservation(
                    reservation,
                    editMode,
                    oldDate,
                    oldStartTime,
                    callback,
                    confirmButton
                )
            }
        }

    }


    private fun setFirstCard(bottomSheetDialog: View) {

        // RESUMED CARD
        val courtName = bottomSheetDialog.findViewById<TextView>(R.id.court_name_confirm_tv)
        courtName?.text = courtViewModel.court.value?.name
        val courtAddress = bottomSheetDialog.findViewById<TextView>(R.id.court_address_confirm_tv)
        courtAddress?.text = String.format(
            getString(R.string.court_address_card),
            courtViewModel.court.value?.address,
            courtViewModel.court.value?.location
        )
        val dateSelected = bottomSheetDialog.findViewById<TextView>(R.id.dateSelected)
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM y")

        val timeSelected: TextView? = bottomSheetDialog.findViewById(R.id.timeSelected)
        val firstTime = courtViewModel.selectedTimes.first().toString()
        val secondTime = courtViewModel.selectedTimes.last().plusHours(1).toString()
        timeSelected?.text = String.format(getString(R.string.time_selected), firstTime, secondTime)

        dateSelected?.text =
            courtViewModel.selectedDay.value?.format(formatter) ?: LocalDate.now().format(formatter)
        val iconSport = bottomSheetDialog.findViewById<ImageView>(R.id.icon_sport)
        iconSport?.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                Sports.sportToIconDrawable(Sports.fromJSON(courtToReserve.sport)!!),
                null
            )
        )
        if (editMode) {
            switch.isChecked = equipment!!
        }
    }

    private fun setAdditionalInformationCard(bottomSheetDialog: View) {
        // ADDITIONAL INFORMATION CARD
        val additionalInfo = bottomSheetDialog.findViewById<TextView>(R.id.additional_body)
        if (!editMode)
            additionalInfo?.text = String.format(
                getString(R.string.informations_body),
                courtToReserve.name,
                courtToReserve.sport.lowercase().replaceFirstChar { it.uppercase() })
        else
            additionalInfo?.text = String.format(
                getString(R.string.informations_body_edit),
                courtToReserve.name,
                courtToReserve.sport.lowercase().replaceFirstChar { it.uppercase() })

    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setPriceDetailCard(bottomSheetDialog: View) {
        val equipmentField = bottomSheetDialog.findViewById<TextView>(R.id.equipment_field)
        equipmentField?.text = String.format(getString(R.string.cost_example, 0))
        val nHours = courtViewModel.selectedTimes.size

        val costField = bottomSheetDialog.findViewById<TextView>(R.id.cost_field)
        val feeHour = courtToReserve.feeHour
        costField?.text = String.format(getString(R.string.cost_example), feeHour * nHours)
        //Evaluate Total Cost
        var totalCost = feeHour * nHours
        val totalCostField = bottomSheetDialog.findViewById<TextView>(R.id.total_euros)
        totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))

        // In Edit Mode if the switch is enabled means that the reservation
        // has equipment selected previously
        if (editMode && switch.isChecked) {
            val feeEquipment = courtToReserve.feeEquipment
            equipmentField?.text =
                String.format(getString(R.string.cost_example, feeEquipment * nHours))
            totalCost = (feeHour + feeEquipment) * nHours
            totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            // If the equipment is not selected the linear layout will go out
            // Else it's visible
            if (isChecked) {
                val feeEquipment = courtToReserve.feeEquipment
                equipmentField?.text =
                    String.format(getString(R.string.cost_example, feeEquipment * nHours))
                totalCost = (feeHour + feeEquipment) * nHours
                totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
            } else {
                equipmentField?.text = String.format(getString(R.string.cost_example, 0))
                totalCost = feeHour * nHours
                totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
            }
        }
    }


    private fun buildAlertDialog(title: String, text: String, context: Context): AlertDialog {

        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun isSheetAlwaysExpanded(): Boolean {
        return true
    }


}