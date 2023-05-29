package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.CourtRepository
import it.polito.mad.buddybench.persistence.repositories.SportRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    private val _loading:MutableLiveData<Boolean> = MutableLiveData(null)
    val loading: LiveData<Boolean> = _loading

    //filters
    var minRating: Float = 0f
    var maxFee: Float = 100f
    var name: String = ""

    var filtersEnabled = false

    @Inject
    lateinit var sportRepository: SportRepository


    private val courtRepositoryFirebase = CourtRepository()

    private val mainScope = MainScope()

    var onFailure: () -> Unit = {}


    fun getAllSports(): LiveData<List<Sports>>{
        _sports.value = listOf(Sports.TENNIS, Sports.BASKETBALL, Sports.FOOTBALL, Sports.VOLLEYBALL)
        return sports
    }
    fun getCourtsBySport(onSuccess: () -> Unit): LiveData<List<CourtDTO>> {
        _loading.value = true

        mainScope.launch {

            courtRepositoryFirebase.getCourtsByDay(selectedSport.value!!, selectedDate, {
                _loading.postValue(false)
                _currentCourts.postValue(listOf())
                onFailure()
            }){
                    list ->
                    _loading.postValue(false)
                    _courts = list
                    println(selectedDate)
                    println( selectedSport.value)
                    val courts = applyFiltersOnCourts(_courts)
                    _currentCourts.postValue(courts)
                    onSuccess()
            }
        }


        return currentCourts
    }

    fun clearFilters(){
        filtersEnabled = false
        minRating= 0f
        maxFee= 100f
        name= ""
    }

    fun applyFilter(clear: Boolean = false){
        filtersEnabled = true
        if(clear)
            clearFilters()
        _currentCourts.value = _courts.filter{
            (it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true))
                    && it.rating >= minRating && it.feeHour <= maxFee
        }.sortedBy { it.name }
    }

    fun setSelectedDate(date: LocalDate){
        selectedDate = date
        getCourtsBySport(){
        }

    }

    fun getSelectedDate(): LocalDate {
        return selectedDate
    }

    fun setSport(sport: Sports){
        selectedSport.value = sport

    }

    private fun applyFiltersOnCourts(courts: List<CourtDTO>): List<CourtDTO> {
        return courts.filter{
            (it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true))
                    && it.rating >= minRating &&  it.feeHour <= maxFee
        }.sortedBy { it.name }
    }


}