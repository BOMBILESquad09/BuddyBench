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
    var minRating: Int = 0
    var maxRating: Int = 5
    var minFee: Int = 0
    var maxFee: Int = 1000
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
            println(sport)
            _courts = courtRepository.getCourtsBySports(Sports.toJSON(sport).uppercase())
            _currentCourts.postValue(_courts)
            println("-- size----------")
            println(_courts.size)
            println("-----sports---------------")
            _courts.forEach{println(it.sport)}
            println("-------------------")
        }.start()
        _currentCourts.value  = listOf()
        return currentCourts
    }

    fun clearFilters(){
        minRating= 0
        maxRating= 5
        minFee= 0
        maxFee= 1000
        name= ""
    }

    fun applyFilter(){
        _currentCourts.value = _courts.filter{
            it.location.contains(name, ignoreCase = true) || it.name.contains(name, ignoreCase = true)
        }
    }

    fun setSelectedDate(date: LocalDate){
        selectedDate = date
        getCourtsBySport(selectedSport.value!!)
    }

    fun getSelectedDate(): LocalDate {
        return selectedDate
    }


}