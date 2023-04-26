package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.CourtTimeDTO
import it.polito.mad.buddybench.dto.CourtTimeTableDTO
import it.polito.mad.buddybench.dto.ReservationDTO
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
import kotlin.math.max

@HiltViewModel
class CourtViewModel @Inject constructor() : ViewModel() {

    private var _initialValue: CourtDTO? = null
    private var _initialOpeningTime = LocalTime.of(0, 0)
    private var _initialClosingTime = LocalTime.of(0, 0)
    private var _initialValueTimeSlots = Utils.getTimeSlots(_initialOpeningTime, _initialClosingTime)
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
    private val _timeSlots: MutableLiveData<List<Pair<LocalTime, Boolean>>> =
        MutableLiveData(listOf())
    private var _plainTimeSlots: MutableLiveData<List<LocalTime>> =
        MutableLiveData(listOf())
    private val plainTimeSlots: LiveData<List<LocalTime>> = _plainTimeSlots
    private val _selectedDay: MutableLiveData<LocalDate> = MutableLiveData(null)
    private val _selectedTimes: MutableLiveData<MutableList<LocalTime>> =
        MutableLiveData(mutableListOf())
    // ** Expose to other classes (view)
    val court: LiveData<CourtDTO> get() = _court
    val days: List<LocalDate> get() = _days
    val timeSlots: LiveData<List<Pair<LocalTime, Boolean>>> get() = _timeSlots
    val selectedDay: LiveData<LocalDate> get() = _selectedDay
    val selectedTimes: LiveData<MutableList<LocalTime>> get() = _selectedTimes

    private val _currentReservation: MutableLiveData<ReservationDTO?> = MutableLiveData(null)
    val currentReservation: LiveData<ReservationDTO?> get() = _currentReservation

    /*already selected slots*/
    var reservationSlots: Pair<LocalTime, LocalTime>? = null
    /**
     * Select day from the list
     * Update UI in the view
     */

    fun setReservationDate(reservationDate: LocalDate){
        _selectedDay.value = reservationDate
    }

    fun selectDay(courtToReserve:CourtDTO, date: LocalDate, reservationDate: LocalDate?): LiveData<LocalDate> {
        getTimeSlotsAvailable(courtToReserve,date, reservationDate)
        _selectedDay.value = date
        return _selectedDay
    }

    fun addSelectedTime(time: LocalTime): MutableList<Int> {
        val differenceList = mutableListOf<Int>()
        val alreadySelected = _timeSlots.value?.filter {
            it.second
        }?.map {
            it.first
        }
        if (
            (alreadySelected != null &&
            alreadySelected.isEmpty())
            || alreadySelected!!.max() == time.minusHours(1)
            || alreadySelected!!.min() == time.plusHours(1)
        ) {
            _timeSlots.value = _timeSlots.value?.mapIndexed {
                index, pair ->
                if (pair.first == time){
                    differenceList.add(index)
                    Pair(time, true)
                } else pair
            }


        } else {
            _timeSlots.value?.forEachIndexed(){
                idx, pair ->
                if (pair.second){
                    differenceList.add(idx)
                }
            }
            _timeSlots.value = _timeSlots.value?.mapIndexed {
                idx, pair ->
                if (pair.first == time){
                    differenceList.add(idx)
                    Pair(time, true)
                } else Pair(pair.first, false)
            }
        }

        return differenceList
    }

    fun selectTimesForEdit(first: LocalTime, last: LocalTime): List<LocalTime> {
        val l = Utils.getTimeSlots(first, last) as MutableList

        //_timeSlots.value = _timeSlots.value?.plus(_selectedTimes.value!!)
        return l
    }

    fun removeSelectedTime(time: Pair<LocalTime, Boolean>): Int? {

        val max = _timeSlots.value?.filter { it.second }?.map{it.first}?.max()
        val min = _timeSlots.value?.filter { it.second }?.map{it.first}?.min()
        var changed: Int? = null
        if(time.first == max || time.first == min){
            _timeSlots.value = _timeSlots.value?.mapIndexed{
                idx, pair ->
                if (pair.first == time.first){
                    changed = idx
                    Pair(pair.first, false)
                } else {
                    pair
                }
            }
        }
        return changed
    }

    fun clearSelectedTime() {
        _selectedTimes.value!!.clear()
    }

    fun getTimeTables(name: String, sport: Sports): LiveData<CourtTimeTableDTO> {
        Thread{
            val tt = courtTimeRepository.getCourtTimeTable(name, sport)
            _timetable.postValue(tt)
            _court.postValue( tt.court)
        }.start()
        return _timetable
    }

    fun getTimeTable(): LiveData<CourtTimeTableDTO> {
        return _timetable
    }


    fun getTimeSlotsAvailable(courtDTO: CourtDTO, date: LocalDate, reservationDate: LocalDate?): LiveData<List<LocalTime>> {
        println(date)
        println(reservationDate)
        println("------------")
        Thread {

            val timeSlotsOccupied = reservationRepository.getTimeSlotsOccupiedForCourtAndDate(courtDTO, date)
            openingAndClosingTimeForCourt(date.dayOfWeek)
            var list = _initialValueTimeSlots.filter { !timeSlotsOccupied.contains(it) } as MutableList
            list.sort()
            if(list.isNotEmpty()) list = Utils.getTimeSlots(list.first(), list.last().plusHours(1)) as MutableList<LocalTime>
            println(list)
            _timeSlots.postValue(list.map{
                Pair(it, false)
            })
            _plainTimeSlots.postValue( list )
            if(reservationDate == date)
                getReservation()
        }.start()
        return plainTimeSlots
    }

    private fun openingAndClosingTimeForCourt(dayOfWeek: DayOfWeek) {
        val courtTime = _timetable.value?.timeTable?.get(dayOfWeek)
        _openingTime.postValue( courtTime?.first ?: LocalTime.of(0, 0))
        _closingTime.postValue(  courtTime?.second ?: LocalTime.of(0, 0))
        _initialValueTimeSlots = if (courtTime != null) Utils.getTimeSlots(courtTime.first, courtTime.second)
        else
            listOf()
        println(_initialValueTimeSlots)

    }

    fun getCourtsBySport(sport: Sports): List<CourtDTO> {
        return courtRepository.getCourtsBySports(Sports.toJSON(sport).uppercase())
    }

    fun getCourtBySportAndName(name: String, sport: Sports): LiveData<CourtDTO> {
        val court = courtRepository.getByNameAndSports(name, sport)
        _court.value = court
        return _court
    }


    fun getReservation(): LiveData<ReservationDTO?> {


        val ts = Utils.getTimeSlots(reservationSlots!!.first, reservationSlots!!.second ).map {
            Pair(it, true)
        }
        val plain = Utils.getTimeSlots(reservationSlots!!.first, reservationSlots!!.second )
        _plainTimeSlots.postValue( (_plainTimeSlots.value?.plus(plain)))
        _timeSlots.postValue(  (_timeSlots.value?.plus(ts)))
        return currentReservation
    }



}