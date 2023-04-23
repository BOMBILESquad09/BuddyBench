package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.entities.toCourtDTO
import it.polito.mad.buddybench.repositories.CourtRepository
import it.polito.mad.buddybench.repositories.CourtTimeRepository
import it.polito.mad.buddybench.repositories.ReservationRepository
import it.polito.mad.buddybench.utils.Utils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CourtViewModel @Inject constructor() : ViewModel() {

    private var _initialValue: Court? = null
    private var _initialOpeningTime = LocalTime.of(6,0)
    private var _initialClosingTime = LocalTime.of(23,0)
    private var _initialValueTimeSlots = Utils.getTimeSlots(_initialOpeningTime, _initialClosingTime)
    private var _court: MutableLiveData<Court> = MutableLiveData(_initialValue)

    @Inject
    lateinit var courtRepository: CourtRepository

    @Inject
    lateinit var reservationRepository: ReservationRepository

    @Inject
    lateinit var courtTimeRepository: CourtTimeRepository

    // ** DateTime pickers

    // Range of days between 2 weeks
    // Range of hours between 8:00 to 23:00 TODO: Include opening hours
    private val _openingTime: MutableLiveData<LocalTime> = MutableLiveData(_initialOpeningTime)
    private val _closingTime: MutableLiveData<LocalTime> = MutableLiveData(_initialClosingTime)
    private val _days = Utils.generateDateRange(LocalDate.now(), LocalDate.now().plusDays(14))
    private val _timeSlots :MutableLiveData<List<LocalTime>> = MutableLiveData(_initialValueTimeSlots)
    private val _selectedDay: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    private val _selectedTime: MutableLiveData<LocalTime> = MutableLiveData(
        _timeSlots.value?.get(0) ?: LocalTime.now())

    // ** Expose to other classes (view)
    val court: LiveData<Court> get() = _court
    val days: List<LocalDate> get() = _days
    val timeSlots: LiveData<List<LocalTime>> get() = _timeSlots
    val selectedDay: LiveData<LocalDate> get() = _selectedDay
    val selectedTime: LiveData<LocalTime> get() = _selectedTime
    val openingTime: LiveData<LocalTime> get() = _openingTime
    val closingTime: LiveData<LocalTime> get() = _closingTime

    /**
     * Get Court
     * @return Court LiveData
     */
    fun getMockCourt(): LiveData<Court> {
        // ** Mock DB

        // Repository Call, All the repos return DTo s
        val courts = courtRepository.getAll()

        // ** TODO: Filter here the right time slots from the opening hours and availability

        _court.value = courts[0].toEntity()

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


    fun getTimeSlotsAvailable(courtDTO: CourtDTO, date: LocalDate): List<LocalTime> {
        val timeSlotsOccupied = reservationRepository.getTimeSlotsOccupiedForCourtAndDate(
            courtDTO,
            date
        )
        openingAndClosingTimeForCourt(courtDTO, date.dayOfWeek)
        return _initialValueTimeSlots.filter {
            !timeSlotsOccupied.contains(it)
        }
    }

    fun openingAndClosingTimeForCourt(courtDTO: CourtDTO, dayOfWeek: DayOfWeek) {
        val courtTime = courtTimeRepository.getCourtTimesByCourt(courtDTO, dayOfWeek)
        _openingTime.value = courtTime?.openingTime
        _closingTime.value = courtTime?.closingTime
        if(courtTime != null)
            _initialValueTimeSlots = Utils.getTimeSlots(courtTime.openingTime, courtTime.closingTime)
        else
            _initialValueTimeSlots = listOf()
    }

}