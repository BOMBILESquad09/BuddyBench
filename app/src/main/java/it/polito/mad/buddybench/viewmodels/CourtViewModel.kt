package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime

class CourtViewModel: ViewModel() {

    private val _initialValue = Court(address = "Via Roma 16, Torino", name = "Tennis Club", feeHour = 12, sport = "Tennis", location = "Torino")
    private val _court: MutableLiveData<Court> = MutableLiveData(_initialValue)

    // ** DateTime pickers

    // Range of days between 2 weeks
    // Range of hours between 8:00 to 23:00 TODO: Include opening hours, and exclude unavailable hours
    private val _days = Utils.generateDateRange(LocalDate.now(), LocalDate.now().plusDays(14))
    private val _timeSlots = Utils.getTimeSlots(LocalTime.parse("06:00"), LocalTime.parse("23:00"))
    private val _selectedDay: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    private val _selectedTime: MutableLiveData<LocalTime> = MutableLiveData(LocalTime.now())

    // ** Expose to other classes (view)
    val court: LiveData<Court> get() = _court
    val days: List<LocalDate> get() = _days
    val timeSlots: List<LocalTime> get() = _timeSlots
    val selectedDay: LiveData<LocalDate> get() = _selectedDay
    val selectedTime: LiveData<LocalTime> get() = _selectedTime

    /**
     * Get Court
     * @return Court LiveData
     */
    fun getMockCourt(): LiveData<Court> {
        // ** Mock DB
        val court = CourtDTO(
            sport = "Tennis",
            name = "Tennis Club #2",
            feeHour = 15,
            address = "Via Roma 19, Torino",
            location = "Torino"
        ).toEntity()

        // ** TODO: Filter here the right time slots from the opening hours and availability

        _court.value = court
        return _court
    }

    /**
     * Select day from the list
     * Update UI in the view
     */
    fun selectDay(date: LocalDate): LiveData<LocalDate> {
        _selectedDay.value = date
        return _selectedDay
    }

    fun selectTime(time: LocalTime): LiveData<LocalTime> {
        _selectedTime.value = time
        return _selectedTime
    }

}