package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.CourtTimeDTO
import it.polito.mad.buddybench.dto.CourtTimeTableDTO
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.entities.toCourtDTO
import it.polito.mad.buddybench.enums.Sports
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

    private var _initialValue: CourtDTO? = null
    private var _initialOpeningTime = LocalTime.of(6, 0)
    private var _initialClosingTime = LocalTime.of(23, 0)
    private var _initialValueTimeSlots =
        Utils.getTimeSlots(_initialOpeningTime, _initialClosingTime)
    private var _court: MutableLiveData<CourtDTO> = MutableLiveData(_initialValue)
    private var _timetable: MutableLiveData<CourtTimeTableDTO> = MutableLiveData(null)
    private var _courtSportsInitial: List<CourtDTO> = listOf()

    @Inject
    lateinit var courtRepository: CourtRepository

    @Inject
    lateinit var reservationRepository: ReservationRepository

    @Inject
    lateinit var courtTimeRepository: CourtTimeRepository

    // ** DateTime pickers

    // Range of days between 2 weeks
    // Range of hours between 8:00 to 23:00
    private val _openingTime: MutableLiveData<LocalTime> = MutableLiveData(_initialOpeningTime)
    private val _closingTime: MutableLiveData<LocalTime> = MutableLiveData(_initialClosingTime)
    private val _days = Utils.generateDateRange(LocalDate.now(), LocalDate.now().plusDays(14))
    private val _timeSlots: MutableLiveData<List<LocalTime>> =
        MutableLiveData(_initialValueTimeSlots)
    private val _selectedDay: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
    private val _selectedTimes: MutableLiveData<MutableList<LocalTime>> =
        MutableLiveData(mutableListOf())

    // ** Expose to other classes (view)
    val court: LiveData<CourtDTO> get() = _court
    val days: List<LocalDate> get() = _days
    val timeSlots: LiveData<List<LocalTime>> get() = _timeSlots
    val selectedDay: LiveData<LocalDate> get() = _selectedDay
    val selectedTimes: LiveData<MutableList<LocalTime>> get() = _selectedTimes


    /**
     * Select day from the list
     * Update UI in the view
     */
    fun selectDay(date: LocalDate): LiveData<LocalDate> {
        _selectedDay.value = date
        return _selectedDay
    }

    fun addSelectedTime(time: LocalTime): LiveData<MutableList<LocalTime>> {

        if (
            _selectedTimes.value!!.isEmpty()
            || _selectedTimes.value!!.last() == time.minusHours(1)
            || _selectedTimes.value!!.first() == time.plusHours(1)
        ) {
            _selectedTimes.value!!.add(time)
            _selectedTimes.value!!.sort()
            val l = _selectedTimes.value!!
            _selectedTimes.value = l
            return _selectedTimes
        } else {
            _selectedTimes.value = mutableListOf()
            _selectedTimes.value!!.add(time)
        }


        return _selectedTimes
    }

    fun removeSelectedTime(time: LocalTime): LiveData<MutableList<LocalTime>> {
        if (_selectedTimes.value!!.last() == time || _selectedTimes.value!!.first() == time
        ) {
            _selectedTimes.value!!.remove(time)
            val l = _selectedTimes.value!!
            _selectedTimes.value = l
            return _selectedTimes
        } else {
            _selectedTimes.value = mutableListOf()
            return _selectedTimes
        }

    }

    fun clearSelectedTime() {
        _selectedTimes.value!!.clear()
    }

    fun getTimeTables(name: String, sport: Sports): LiveData<CourtTimeTableDTO> {
        _timetable.value = courtTimeRepository.getCourtTimeTable(name, sport)
        _court.value = _timetable.value!!.court
        return _timetable
    }

    fun getTimeTable(): LiveData<CourtTimeTableDTO> {
        return _timetable
    }


    fun getTimeSlotsAvailable(courtDTO: CourtDTO, date: LocalDate): List<LocalTime> {
        val timeSlotsOccupied = reservationRepository.getTimeSlotsOccupiedForCourtAndDate(
            courtDTO,
            date
        )
        openingAndClosingTimeForCourt(date.dayOfWeek)
        val list = _initialValueTimeSlots.filter {
            !timeSlotsOccupied.contains(it)
        } as MutableList
        if (list.isNotEmpty())
            list.removeLast()

        _timeSlots.value = list

        return list
    }

    fun openingAndClosingTimeForCourt(dayOfWeek: DayOfWeek) {

        val courtTime = _timetable.value?.timeTable?.get(dayOfWeek)
        _openingTime.value = courtTime?.first ?: LocalTime.of(0, 0)
        _closingTime.value = courtTime?.second ?: LocalTime.of(0, 0)
        _initialValueTimeSlots = if (courtTime != null)
            Utils.getTimeSlots(courtTime.first, courtTime.second)
        else
            listOf()
    }

    fun getCourtsBySport(sport: Sports): List<CourtDTO> {
        return courtRepository.getCourtsBySports(Sports.toJSON(sport).uppercase())
    }


}