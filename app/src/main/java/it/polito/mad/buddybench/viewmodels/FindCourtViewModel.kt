package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.repositories.CourtRepository
import it.polito.mad.buddybench.persistence.repositories.CourtTimeRepository
import it.polito.mad.buddybench.persistence.repositories.SportRepository
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FindCourtViewModel @Inject constructor(): ViewModel() {
    private var _sports: MutableLiveData<List<Sports>> = MutableLiveData(listOf())
    var sports: LiveData<List<Sports>> = _sports
    var selectedSport: MutableLiveData<Sports> = MutableLiveData(null) // Added default value

    private var _courts: List<CourtDTO> = listOf()
    private val _currentCourts: MutableLiveData<List<CourtDTO>> = MutableLiveData(listOf())
    val currentCourts: LiveData<List<CourtDTO>> = _currentCourts

    private var selectedDate: LocalDate = LocalDate.now()
    var loading = MutableLiveData(false)

    //filters
    var minRating: Float = 0f
    var maxFee: Float = 100f
    var name: String = ""


    @Inject
    lateinit var sportRepository: SportRepository
    @Inject
    lateinit var courtRepository: CourtRepository
    @Inject
    lateinit var courtTimeRepository: CourtTimeRepository


    fun getAllSports(): LiveData<List<Sports>>{
        Thread{
            _sports.postValue(  sportRepository.getAll().map { Sports.valueOf(it.name) })
        }.start()
        return sports
    }
    fun getCourtsBySport(): LiveData<List<CourtDTO>> {
        Thread{
            loading.postValue(true)
            _courts = courtTimeRepository.getCourtTimesByDay(selectedSport.value!!, selectedDate)
            val courts = applyFiltersOnCourts(_courts)
            _currentCourts.postValue(courts)
        }.start()
        return currentCourts
    }

    fun clearFilters(){
        minRating= 0f
        maxFee= 100f
        name= ""
    }

    fun applyFilter(){
        _currentCourts.value = _courts.filter{
            (it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true))
                    && it.rating >= minRating && it.feeHour <= maxFee
        }.sortedBy { it.name }
    }

    fun setSelectedDate(date: LocalDate){
        selectedDate = date
        getCourtsBySport()

    }

    fun getSelectedDate(): LocalDate {
        return selectedDate
    }

    fun setSport(sport: Sports){
        selectedSport.value = sport
        getCourtsBySport()
    }

    fun applyFiltersOnCourts(courts: List<CourtDTO>): List<CourtDTO> {
        return courts.filter{
            (it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true))
                    && it.rating >= minRating &&  it.feeHour <= maxFee
        }.sortedBy { it.name }
    }
}