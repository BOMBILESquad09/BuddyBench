package it.polito.mad.buddybench.activities.court

import android.R.attr.duration
import android.R.attr.text
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.view.WeekCalendarView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentCourtBinding
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.WeeklyCalendarAdapter
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject
import java.io.FileNotFoundException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@AndroidEntryPoint
class CourtFragment() : Fragment(R.layout.fragment_court) {

    // ** NB: Autogenerated binding class containing all the elements of the .xml file
    // with an id. Example: binding.court_name_tv
    private var _binding: FragmentCourtBinding? = null

    // ** This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var user: UserDTO
    private lateinit var courtToReserve: CourtDTO
    private lateinit var selectedDate: LocalDate

    // ** Court LiveData by ViewModel
    private val courtViewModel by viewModels<CourtViewModel>()
    private val reservationViewModel by viewModels<ReservationViewModel>()
    val userViewModel by viewModels<UserViewModel>()

    private lateinit var profile: Profile
    private lateinit var sharedPref: SharedPreferences

    private lateinit var courtName: String
    private lateinit var sport: Sports

    // ** Edit mode (default to false)
    private var editMode = false
    private var reservationDate: LocalDate? = null
    private var emailReservation: String = ""
    private var startTime: Int = -1
    private var endTime: Int = -1
    private lateinit var recyclerView: RecyclerView

    private lateinit var recyclerWeeklyCalendarView: RecyclerView
    private lateinit var weeklyDays: MutableList<Pair<LocalDate, Boolean>>
    private lateinit var switch: Switch

     private var oldDate: LocalDate? = null
     private var oldStartTime: LocalTime? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        editMode = arguments?.getBoolean("edit", false) ?: false
        arguments?.getString("date").let {
            if (it != null)
                reservationDate = LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        }

        emailReservation = arguments?.getString("email", "") ?: ""
        startTime = arguments?.getInt("startTime", -1) ?: -1
        endTime = arguments?.getInt("endTime", -1) ?: -1
        val s = arguments?.getString("date", LocalDate.now().format(DateTimeFormatter.ISO_DATE)) ?: LocalDate.now().toString()
        selectedDate = LocalDate.parse(s, DateTimeFormatter.ISO_DATE)
        _binding = FragmentCourtBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ** View Model
        courtName = activity?.intent?.getStringExtra("courtName") ?: "Central Park Tennis"
        sport = Sports.valueOf(
            activity?.intent?.getStringExtra("sport")?.uppercase() ?: Sports.TENNIS.name
        )
        if (editMode) {
            editMode()
        }



        weeklyDays = (0..30).map {
            val day = LocalDate.now().plusDays(it.toLong())
            if (day == selectedDate) {
                Pair(selectedDate, true)
            } else {
                Pair(day, false)
            }
        } as MutableList<Pair<LocalDate, Boolean>>

        // Callback used inside the ViewHolder Item of the Recycler View


        // Setting the Manager Layout for the RecyclerView
        recyclerView = view.findViewById(R.id.time_slot_grid)
//        recyclerWeeklyCalendarView = view.findViewById(R.id.weekly_calendar_adapter)

        val calendarView = view.findViewById<WeekCalendarView>(R.id.calendar)


        val calendarCallback: (LocalDate, LocalDate) -> Unit = { last, new ->
            if (last == new) {
                calendarView.notifyDayChanged(WeekDay(last, WeekDayPosition.InDate))
            } else {
                calendarView.notifyDayChanged(WeekDay(new, WeekDayPosition.InDate))
                calendarView.notifyDayChanged(WeekDay(last, WeekDayPosition.InDate))
                courtViewModel.selectDay(courtToReserve, new, reservationDate)
                selectedDate = new
            }
        }

        calendarView.dayBinder = WeeklyCalendarDayBinder(selectedDate, calendarCallback)
        calendarView.setup(weeklyDays.first().first, weeklyDays.last().first, DayOfWeek.MONDAY)
        calendarView.scrollToDate(selectedDate)


        val selectedPosition = (Period.between(LocalDate.now(), selectedDate)).days
        /*recyclerWeeklyCalendarView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerWeeklyCalendarView.adapter = WeeklyCalendarAdapter(weeklyDays, selectedPosition, calendarCallback)
        LinearSnapHelper().attachToRecyclerView(recyclerWeeklyCalendarView)
        recyclerWeeklyCalendarView.scrollToPosition(selectedPosition)*/


        val callback: (Pair<LocalTime, Boolean>) -> Unit = { selected ->
            courtViewModel.timeSlots.value?.find {
                it.first == selected.first
            }.let {
                it!!
                if (!it.second) {
                    val diff = courtViewModel.addSelectedTime(it.first)
                    for (i in diff)
                        recyclerView.adapter?.notifyItemChanged(i)
                } else {
                    val changedItem = courtViewModel.removeSelectedTime(it)
                    if (changedItem != null)
                        recyclerView.adapter?.notifyItemChanged(changedItem)
                }
            }
        }
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = TimeSlotGripAdapter(
            courtViewModel.timeSlots,
            callback,
        )
        // Return to the previous activity
        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        // Retrieve the time tables associated to a Court
        courtViewModel.getTimeTables(courtName, sport).observe(viewLifecycleOwner) {
            if (it == null) return@observe
            updateView(it.court)
            courtViewModel.getTimeSlotsAvailable(it.court, selectedDate, reservationDate)
                .observe(viewLifecycleOwner) { timeSlots ->
                    if (timeSlots == null) return@observe
                    if (timeSlots.isEmpty()) {
                        binding.root.findViewById<ConstraintLayout>(R.id.empty_timeslots).visibility =
                            View.VISIBLE
                    } else {
                        binding.root.findViewById<ConstraintLayout>(R.id.empty_timeslots).visibility =
                            View.GONE
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }

        }


        // ** Navigate to court reservation
        binding.buttonFirst.setOnClickListener {
            if (courtViewModel.selectedTimes.isEmpty()) {
                val textError = getString(R.string.error_book)
                buildAlertDialog("Book Error", textError, requireContext()).show()
            } else {
                showBottomSheetDialog()
            }

        }
        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )!!
        profile =
            Profile.fromJSON(JSONObject(sharedPref.getString("profile", Profile.mockJSON())!!))
        user = profile.toUserDto()


    }

    private fun updateView(court: CourtDTO) {
        binding.courtNameTv.text = court.name.replace("Courts", "")
        binding.courtAddressTv.text = court.address + ", " + court.location
        binding.courtFeeTv.text = getString(R.string.court_fee, court.feeHour.toString())
        courtViewModel.getTimeTable().value?.timeTable.let {
            if (it != null) {
                binding.courtOpeningHoursTv.text = Utils.getStringifyTimeTable(it)
            }
        }
        val bitmap = try {
            BitmapFactory.decodeStream(view?.context?.assets?.open("courtImages/" + court.path + ".jpg"))
        } catch (_: FileNotFoundException) {
            BitmapFactory.decodeStream(view?.context?.assets?.open("courtImages/default_image.jpg"))
        }
        courtToReserve = court
        binding.backgroundImage.setImageBitmap(bitmap)
        binding.rating.text = court.rating.toString()
        binding.ratingBar.rating = court.rating.toFloat()
        binding.nReviews.text = "(${court.nReviews})"
        binding.equipmentCost.text = String.format(
            getString(R.string.equipment_phrase),
            court.feeEquipment
        );
        binding.sportIconEquipment.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                Sports.sportToIconDrawable(
                    Sports.fromJSON(
                        court.sport
                    )!!
                ),
                null
            )
        )
        if(!editMode) {
            val b : Button? = view?.findViewById(R.id.cancel_button)
            b?.visibility = View.GONE
        } else {
            reservationViewModel.getReservation(
                courtToReserve.name,
                Sports.fromJSON(courtToReserve.sport)!!,
                profile.email,
                selectedDate,
                startTime
            )
            val b : Button? = view?.findViewById(R.id.cancel_button)
            b?.setOnClickListener {
                val textConfirm = String.format(
                    getString(R.string.confirm_delete),
                    courtToReserve.name,
                    courtToReserve.sport,
                    reservationViewModel.currentReservation.value!!.startTime,
                    reservationViewModel.currentReservation.value!!.endTime)
                val alertDialog = buildAlertDialogDelete("Confirm Delete", textConfirm, requireContext())
                alertDialog.show()
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

    private fun buildAlertDialogDelete(title: String, text: String, context: Context): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton("Yes") { dialog, _ ->
                reservationViewModel.deleteReservation(
                    reservationViewModel.currentReservation.value!!
                )
                dialog.dismiss()
                requireActivity().finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun showBottomSheetDialog() {

        // Set the constructor of bottom dialog with the content
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_court_confirm)

        // Take the reference for the switch of equipment
        switch = bottomSheetDialog.findViewById(R.id.switch_equipment)!!

        // Setting up the three card
        // Setting the values for the first Card in dialog
        setFirstCard(bottomSheetDialog)
        // Setting up the Additional Information Card
        setAdditionalInformationCard(bottomSheetDialog)
        // Setting the price details Dialog
        setPriceDetailCard(bottomSheetDialog)

        // CheckBox inside the Additional Information Card
        val checkboxAccept = bottomSheetDialog.findViewById<CheckBox>(R.id.accept_checkbox)
        // Take the reference of the confirm button
        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirmPrenotation)
        confirmButton?.setOnClickListener {
            if (!checkboxAccept!!.isChecked) {
                val textError = String.format(getString(R.string.error_info), courtToReserve.name)
                buildAlertDialog(
                    "Additional Information",
                    textError,
                    bottomSheetDialog.context
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


                reservationViewModel.saveReservation(reservation, editMode, oldDate, oldStartTime)
                requireActivity().finish()
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun editMode() {
        courtViewModel.setReservationDate(reservationDate!!)
        val b = view?.findViewById<Button>(R.id.button_first)
        b?.text = "Edit Book"
        courtViewModel.startTime = LocalTime.of(startTime,0)
        courtViewModel.endTime = LocalTime.of(endTime,0)
        oldStartTime =  LocalTime.of(startTime,0)
        oldDate = selectedDate


    }

    private fun setFirstCard(bottomSheetDialog: BottomSheetDialog) {

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
        if(editMode) {
            switch.isChecked = reservationViewModel.currentReservation.value!!.equipment
        }
    }

    private fun setAdditionalInformationCard(bottomSheetDialog: BottomSheetDialog) {
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
    private fun setPriceDetailCard(bottomSheetDialog: BottomSheetDialog) {
        val equipmentField = bottomSheetDialog.findViewById<TextView>(R.id.equipment_field)
        equipmentField?.text = String.format(getString(R.string.cost_example, 0))

        val costField = bottomSheetDialog.findViewById<TextView>(R.id.cost_field)
        val feeHour = courtToReserve.feeHour
        costField?.text = String.format(getString(R.string.cost_example), feeHour)
        //Evaluate Total Cost
        val nHours = courtViewModel.selectedTimes.size
        var totalCost = feeHour * nHours
        val totalCostField = bottomSheetDialog.findViewById<TextView>(R.id.total_euros)
        totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))

        // In Edit Mode if the switch is enabled means that the reservation
        // has equipment selected previously
        if(editMode && switch.isChecked) {
            val feeEquipment = courtToReserve.feeEquipment
            equipmentField?.text = String.format(getString(R.string.cost_example, feeEquipment))
            totalCost = (feeHour + feeEquipment) * nHours
            totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            // If the equipment is not selected the linear layout will go out
            // Else it's visible
            if (isChecked) {
                val feeEquipment = courtToReserve.feeEquipment
                equipmentField?.text = String.format(getString(R.string.cost_example, feeEquipment))
                totalCost = (feeHour + feeEquipment) * nHours
                totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
            } else {
                equipmentField?.text = String.format(getString(R.string.cost_example, 0))
                totalCost = feeHour * nHours
                totalCostField?.text = String.format(getString(R.string.cost_example, totalCost))
            }
        }
    }

}