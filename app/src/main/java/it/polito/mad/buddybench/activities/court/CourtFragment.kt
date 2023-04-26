package it.polito.mad.buddybench.activities.court

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.databinding.FragmentCourtBinding
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import it.polito.mad.buddybench.viewmodels.CourtViewModel
import it.polito.mad.buddybench.viewmodels.ReservationViewModel
import it.polito.mad.buddybench.viewmodels.UserViewModel
import org.json.JSONObject
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.LocalTime
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

    // ** Edit mode (default to false)
    private var editMode = false
    private var reservationDate: String = LocalDate.now().toString()
    private var emailReservation: String = ""
    private var startTime: Int = -1
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        editMode = arguments?.getBoolean("edit", false) ?: false
        reservationDate = arguments?.getString("date", LocalDate.now().toString()).toString()
        emailReservation = arguments?.getString("email", "") ?: ""
        startTime = arguments?.getInt("startTime", -1) ?: -1




        _binding = FragmentCourtBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ** View Model
        val courtName = activity?.intent?.getStringExtra("courtName") ?: "Central Park Tennis"
        val sport = Sports.valueOf(
            activity?.intent?.getStringExtra("sport")?.uppercase() ?: Sports.TENNIS.name
        )
        selectedDate = LocalDate.now()
        if (editMode == true) {
            editMode(courtName, sport)
        }

        // Callback used inside the ViewHolder Item of the Recycler View


        if (editMode) {

            editMode(courtName, sport)

        }


        // Setting the Manager Layout for the RecyclerView
        recyclerView = view.findViewById(R.id.time_slot_grid)

        val callback: (  Pair<LocalTime, Boolean>) -> Unit = { selected ->
            courtViewModel.timeSlots.value?.find {
                it.first == selected.first
            }.let {
                it!!
                if (!it.second){
                    val diff = courtViewModel.addSelectedTime(it.first)
                    for (i in diff)
                        recyclerView.adapter?.notifyItemChanged(i)
                } else{
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

            courtViewModel.getTimeSlotsAvailable(it.court, selectedDate)
                .observe(viewLifecycleOwner){
                    if (it == null) return@observe
                    recyclerView.adapter?.notifyDataSetChanged()
            }
        }


        // ** Navigate to court reservation
        binding.buttonFirst.setOnClickListener {
            showBottomSheetDialog()
        }

        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )!!
        profile =
            Profile.fromJSON(JSONObject(sharedPref.getString("profile", Profile.mockJSON())!!))
        user = profile.toUserDto()

        if (editMode) {
            val b = view.findViewById<Button>(R.id.button_first)
            b.text = "Edit Book"
        }

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
            courtViewModel.court.value?.feeEquipment
        );
    }

    private fun renderDayItem(day: LocalDate, selected: LocalDate) {
        val dayScrollItem =
            layoutInflater.inflate(
                R.layout.datepicker_scroll_item,
                binding.daysScrollView,
                false
            )
        val dayTv: TextView = dayScrollItem.findViewById(R.id.day_tv)
        val dayOfMonthTv: TextView = dayScrollItem.findViewById(R.id.day_of_month_tv)
        val monthTv: TextView = dayScrollItem.findViewById(R.id.month_tv)

        dayTv.text = Utils.capitalize(day.dayOfWeek.name.subSequence(0, 3).toString())
        dayOfMonthTv.text = day.dayOfMonth.toString()
        monthTv.text = Utils.capitalize(day.month.name.subSequence(0, 3).toString())

        // ** Selected day
        if (day == selected) {
            val primaryColor =
                ContextCompat.getColor(requireContext(), R.color.md_theme_light_primary)
            val whiteColor =
                ContextCompat.getColor(requireContext(), R.color.md_theme_light_background)
            dayOfMonthTv.background.setTint(primaryColor)
            dayOfMonthTv.setTextColor(whiteColor)
        }

        // ** Last item no margin at the end
        if (day == courtViewModel.days.last()) {
            val noMarginParams =
                MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            noMarginParams.marginEnd = 0
            dayScrollItem.layoutParams = noMarginParams
        }

        courtViewModel.clearSelectedTime()

        // ** OnClick Listener
        dayScrollItem.setOnClickListener { courtViewModel.selectDay(day) }

        binding.daysScrollView.addView(dayScrollItem)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_court_confirm)
        val courtName = bottomSheetDialog.findViewById<TextView>(R.id.court_name_confirm_tv)
        courtName?.text = courtViewModel.court.value?.name
        val courtAddress = bottomSheetDialog.findViewById<TextView>(R.id.court_address_confirm_tv)
        courtAddress?.text = courtViewModel.court.value?.address + ", " + courtViewModel.court.value?.location
        val dateSelected = bottomSheetDialog.findViewById<TextView>(R.id.dateSelected)
        dateSelected?.text = courtViewModel.selectedDay.value!!.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))
        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirmPrenotation)

        val totalCost = bottomSheetDialog.findViewById<TextView>(R.id.total_cost)
        val feeHour = courtViewModel.court.value!!.feeHour
        val feeEquipment = courtViewModel.court.value!!.feeEquipment
        val nHours = courtViewModel.selectedTimes.value!!.size



        val s = " $nHours h x $feeHour = ${feeHour * nHours} €/h"

        totalCost?.text = String.format(getString(R.string.total_price), s);
        val switch = bottomSheetDialog.findViewById<Switch>(R.id.switch_equipment)

        switch?.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                val sEquip =
                    " $nHours h x ($feeHour €/h + $feeEquipment €/h) = ${(feeHour + feeEquipment) * nHours} €/h"
                totalCost?.text = String.format(getString(R.string.total_price), sEquip);
            } else {
                totalCost?.text = String.format(getString(R.string.total_price), s);
            }
        }

        confirmButton?.setOnClickListener {



            val reservation = ReservationDTO(
                userOrganizer = user,
                court = courtToReserve,
                date = courtViewModel.selectedDay.value!!,
                startTime = courtViewModel.selectedTimes.value!!.first(),
                endTime = courtViewModel.selectedTimes.value!!.last(),
                equipment = switch!!.isChecked
            )
            reservationViewModel.saveReservation(reservation)

            requireActivity().finish()

            bottomSheetDialog.dismiss()

        }

        val timeSelected = bottomSheetDialog.findViewById<TextView>(R.id.timeSelected)
        // TODO: Visualize all the prenotation inside the dialog (for the moment just the first)
        val hourSelected = courtViewModel.selectedTimes.value!![0]
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        timeSelected?.text = hourSelected.format(formatter) + " - " + hourSelected.plusHours(1).format(formatter)

        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun editMode(courtName: String, sport: Sports){
        selectedDate = LocalDate.parse(reservationDate)

        reservationViewModel.getReservation(
            courtName,
            sport,
            emailReservation,
            LocalDate.parse(reservationDate),
            startTime
        ).observe(viewLifecycleOwner){
            if (it == null) return@observe
            courtViewModel.selectTimesForEdit(
                it.startTime,
                it.endTime
            )

        }

    }
}

//val drawable =
//    Sports.sportToIconDrawable(Sports.fromJSON(courtViewModel.court.value!!.sport)!!)


//        val sportDrawable = ContextCompat.getDrawable(requireContext(), drawable)
//        sportDrawable!!.mutate().setBounds(10, 10, 10, 10)
//        binding.equipmentCost.setCompoundDrawables(
//            sportDrawable, null, null, null
//        )

//        val wrappedDrawable = DrawableCompat.wrap(iconDrawable!!)
//        wrappedDrawable.mutate().setTint(Color.WHITE)
//        val bitmap = wrappedDrawable.toBitmap(160, 160)
//        b.setImageBitmap(bitmap)