package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.repositories.CourtRepository
import it.polito.mad.buddybench.repositories.SportRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FindCourtViewModel @Inject constructor(): ViewModel() {
    private var _sports: MutableLiveData<List<Sports>> = MutableLiveData(listOf())
    var sports: LiveData<List<Sports>> = _sports
    var selectedSport: MutableLiveData<Sports> = MutableLiveData(null)

    private var _courts: List<CourtDTO> = listOf()
    private val _currentCourts: MutableLiveData<List<CourtDTO>> = MutableLiveData(listOf())
    val currentCourts: LiveData<List<CourtDTO>> = _currentCourts

    private var selectedDate: LocalDate = LocalDate.now()



    //filters
    var minRating: Float = 0f
    var maxRating: Float = 5f
    var minFee: Float = 0f
    var maxFee: Float = 1000f
    var name: String = ""


    @Inject
    lateinit var sportRepository: SportRepository
    @Inject
    lateinit var courtRepository: CourtRepository
    fun getAllSports(): LiveData<List<Sports>>{
        Thread{
            _sports.postValue(  sportRepository.getAll().map { Sports.valueOf(it.name) })
        }.start()
        return sports
    }
    fun getCourtsBySport(sport: Sports): LiveData<List<CourtDTO>> {
        Thread{
            _courts = courtRepository.getCourtsBySports(Sports.toJSON(sport).uppercase())
            _currentCourts.postValue(_courts.sortedBy { it.name })
        }.start()
        return currentCourts
    }

    fun clearFilters(){
        minRating= 0f
        maxRating= 5f
        minFee= 0f
        maxFee= 1000f
        name= ""
    }

    fun applyFilter(){
        _currentCourts.value = _courts.filter{
            (it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true))
                    && it.rating >= minRating && it.rating <= maxRating
                    && it.feeHour >= minFee && it.feeHour <= maxFee
        }.sortedBy { it.name }
    }

    fun setSelectedDate(date: LocalDate){
        selectedDate = date
        getCourtsBySport(selectedSport.value!!)
    }

    fun getSelectedDate(): LocalDate {
        return selectedDate
    }


}