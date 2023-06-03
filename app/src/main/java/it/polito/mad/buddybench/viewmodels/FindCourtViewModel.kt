package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.activities.findcourt.FindStates
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.CourtRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.ReservationRepository
import it.polito.mad.buddybench.persistence.repositories.SportRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FindCourtViewModel @Inject constructor() : ViewModel() {
    private var _sports: MutableLiveData<List<Sports>> = MutableLiveData(listOf())
    var sports: LiveData<List<Sports>> = _sports
    var selectedSport: MutableLiveData<Sports> = MutableLiveData(null) // Added default value

    private var _courts: List<CourtDTO> = listOf()
    private val _currentCourts: MutableLiveData<List<CourtDTO>> = MutableLiveData(listOf())
    val currentCourts: LiveData<List<CourtDTO>> = _currentCourts

    private var selectedDate: LocalDate = LocalDate.now()
    private val _loading: MutableLiveData<Boolean> = MutableLiveData(null)
    val loading: LiveData<Boolean> = _loading

    private val _findState: MutableLiveData<FindStates> = MutableLiveData(FindStates.COURTS)
    val findStates: LiveData<FindStates> = _findState

    //filters
    var minRating: Float = 0f
    var maxFee: Float = 100f
    var name: String = ""

    var filtersEnabled = false

    @Inject
    lateinit var sportRepository: SportRepository


    private val courtRepository = CourtRepository()
    private val reservationRepository = ReservationRepository()


    var onFailure: () -> Unit = {}


    fun getAllSports(): LiveData<List<Sports>> {
        _sports.value = listOf(Sports.TENNIS, Sports.BASKETBALL, Sports.FOOTBALL, Sports.VOLLEYBALL)
        return sports
    }

    private fun getCourtsBySport(onSuccess: () -> Unit): LiveData<List<CourtDTO>> {
        _loading.value = true

        viewModelScope.launch {

            courtRepository.getCourtsByDay(selectedSport.value!!, selectedDate, {
                _loading.postValue(false)
                _currentCourts.postValue(listOf())
                onFailure()

            }) { list ->
                _loading.postValue(false)
                _courts = list
                val courts = applyFiltersOnCourts(_courts)
                _currentCourts.postValue(courts)
                onSuccess()
            }
        }


        return currentCourts
    }

    fun clearFilters() {
        filtersEnabled = false
        minRating = 0f
        maxFee = 100f
    }


    fun applyFilterOnCourts(clear: Boolean = false) {
        filtersEnabled = true
        if (clear)
            clearFilters()
        _currentCourts.value = _courts.filter {
            (it.location.contains(name, ignoreCase = true) || it.name.contains(
                name,
                ignoreCase = true
            ))
                    && it.rating >= minRating && it.feeHour <= maxFee
        }.sortedBy { it.name }
    }

    fun setSelectedDate(date: LocalDate) {
        selectedDate = date
        getCourtsOrPublicGames()

    }

    fun getSelectedDate(): LocalDate {
        return selectedDate
    }

    fun setSport(sport: Sports) {
        selectedSport.value = sport

    }

    private fun applyFiltersOnCourts(courts: List<CourtDTO>): List<CourtDTO> {
        return courts.filter {
            (it.location.contains(name, ignoreCase = true) || it.name.contains(
                name,
                ignoreCase = true
            ))
                    && it.rating >= minRating && it.feeHour <= maxFee
        }.sortedBy { it.name }
    }


    private var _publicGames: List<ReservationDTO> = listOf()
    private val _currentPublicGames: MutableLiveData<List<ReservationDTO>> =
        MutableLiveData(listOf())
    val currentPublicGames: LiveData<List<ReservationDTO>> = _currentPublicGames

    private val reservationRepositoryFirebase = ReservationRepository()


    private fun getPublicGamesBySport(
        refresh: Boolean = true,
        onSuccess: () -> Unit
    ): LiveData<List<ReservationDTO>> {
        _loading.value = refresh

        viewModelScope.launch {

            reservationRepositoryFirebase.getPublicGames(
                selectedDate,
                selectedSport.value!!.toString(),
                {
                    _loading.postValue(false)
                    _currentPublicGames.postValue(listOf())
                    onFailure()
                }) { list ->
                _loading.postValue(false)
                _publicGames = list
                val publicGames = applyFiltersOnPublicGames(_publicGames)
                _currentPublicGames.postValue(publicGames)
                onSuccess()
            }
        }

        return currentPublicGames
    }

    private fun applyFilterOnPublicGames(clear: Boolean = false) {
        filtersEnabled = true
        if (clear)
            clearFilters()
        _currentPublicGames.value = _publicGames.filter {
            (it.court.location.contains(name, ignoreCase = true) || it.court.name.contains(
                name,
                ignoreCase = true
            ))
                    && it.court.rating >= minRating && it.court.feeHour <= maxFee
        }.sortedBy { it.court.name }
    }


    fun setFindState(id: Int) {
        _findState.value = when (id) {
            0 -> FindStates.COURTS
            1 -> FindStates.GAMES
            else -> FindStates.COURTS
        }
    }


    private fun applyFiltersOnPublicGames(publicGames: List<ReservationDTO>): List<ReservationDTO> {
        return publicGames.filter {
            (it.court.location.contains(name, ignoreCase = true) || it.court.name.contains(
                name,
                ignoreCase = true
            ))
                    && it.court.rating >= minRating && it.court.feeHour <= maxFee
        }.sortedBy { it.court.name }
    }

    fun getCourtsOrPublicGames() {
        when (findStates.value!!) {
            FindStates.COURTS -> getCourtsBySport { }
            FindStates.GAMES -> getPublicGamesBySport { }
        }
    }

    fun applyFilterOnCourtsOrPublicGames(clear: Boolean = false) {
        when (findStates.value!!) {
            FindStates.COURTS -> applyFilterOnCourts()
            FindStates.GAMES -> applyFilterOnPublicGames()
        }

    }

    fun sendJoinRequest(reservationDTO: ReservationDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            reservationRepository.sendRequestToJoin(reservationDTO, onFailure) {
                getPublicGamesBySport(false) {
                    onSuccess()
                }
            }
        }
    }

    fun withdrawJoinRequest(reservationDTO: ReservationDTO, onSuccess: () -> Unit) {
        viewModelScope.launch {
            reservationRepository.rejectJoinRequest(
                reservationDTO,
                Firebase.auth.currentUser!!.email!!,
                onFailure
            ) {
                getPublicGamesBySport(false) {
                    println("------------------------------fdddddddddddd-----------------------")
                    onSuccess()

                }
            }
        }
    }


}