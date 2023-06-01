package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.TimeSlotsNotAvailableException
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.enums.Visibilities
import it.polito.mad.buddybench.persistence.dto.ReservationDTO

import it.polito.mad.buddybench.persistence.firebaseRepositories.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor() : ViewModel() {

    private val _reservations: MutableLiveData<HashMap<LocalDate, List<ReservationDTO>>> =
        MutableLiveData(null)
    private val _selectedDate: MutableLiveData<LocalDate> = MutableLiveData(null)
    val selectedDate: LiveData<LocalDate> = _selectedDate
    var oldDate: LocalDate? = _selectedDate.value

    private val _currentReservation: MutableLiveData<ReservationDTO?> = MutableLiveData(null)
    val currentReservation: LiveData<ReservationDTO?> get() = _currentReservation
    val loading: MutableLiveData<Boolean> = MutableLiveData(null)

    var refresh: Boolean = false
    var email: String = ""

    lateinit var profile: Profile
    var onFailure = {}

    private val reservationRepository = ReservationRepository()

    // ** Expose to other classes (view)
    val reservations: LiveData<HashMap<LocalDate, List<ReservationDTO>>> get() = _reservations

    private val _pendingFriends: MutableLiveData<List<Pair<Profile, Boolean>>> =
        MutableLiveData(listOf())
    var oldPendingFriends: List<Pair<Profile, Boolean>> = _pendingFriends.value!!
    val pendingFriends: LiveData<List<Pair<Profile, Boolean>>> = _pendingFriends

    private val _acceptedFriends: MutableLiveData<List<Pair<Profile, Boolean>>> =
        MutableLiveData(listOf())
    var oldAcceptedFriends: List<Pair<Profile, Boolean>> = _acceptedFriends.value!!
    val acceptedFriends: LiveData<List<Pair<Profile, Boolean>>> = _acceptedFriends

    private val _notInvitedFriends: MutableLiveData<List<Pair<Profile, Boolean>>> =
        MutableLiveData(listOf())
    var oldNotInvitedFriends: List<Pair<Profile, Boolean>> = _notInvitedFriends.value!!
    val notInvitedFriends: LiveData<List<Pair<Profile, Boolean>>> = _notInvitedFriends

    private val mainScope = viewModelScope

    fun setVisibility(
        reservationDTO: ReservationDTO,
        visibilities: Visibilities,
        onFailure: () -> Unit,
        onSuccess: (Visibilities) -> Unit
    ) {
        mainScope.launch {
            reservationRepository.setVisibility(
                reservationDTO,
                visibilities,
                onFailure,
                onSuccess
            )
        }
    }

    fun getAllByUser(refresh:Boolean = false): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        loading.value = !refresh
        mainScope.launch {
            reservationRepository.getAllByUser({

                loading.postValue(false)
                _reservations.postValue(hashMapOf())
                onFailure()


            }) {
                loading.postValue(false)
                _reservations.postValue(it)
            }
        }


        return _reservations
    }

    fun saveReservation(
        reservation: ReservationDTO,
        edit: Boolean,
        oldDate: LocalDate?,
        onFailure: () -> Unit,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        loading.value = true


        mainScope.launch {
            try {
                if (!edit) {
                    try {
                        reservationRepository.save(reservation, onFailure, onError = onError) {
                            loading.postValue(false)
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        onFailure()

                    }
                } else {
                    reservationRepository.update(
                        reservation,
                        oldDate!!, onFailure, onError = onError
                    ) {
                        loading.postValue(false)
                        onSuccess()
                    }

                }
            } catch (e: Exception) {
                onFailure()
            }
        }
    }


    fun updateSelectedDay(date: LocalDate) {
        oldDate = _selectedDate.value
        _selectedDate.value = date

    }

    fun getSelectedReservations(): List<ReservationDTO>? {
        return reservations.value?.get(selectedDate.value ?: LocalDate.now())
    }

    fun getReservation(
        reservationID: String,
        isOrganizer: Boolean = true,
    ): MutableLiveData<ReservationDTO?> {
        viewModelScope.launch {
            val res = reservationRepository.getReservation(reservationID, onFailure = onFailure)
            if (res != null) {
                _currentReservation.postValue(res)
                initAcceptedFriends(res)
                initPendingFriends(res)
                initNotInvitedFriends()
            }
        }
        return _currentReservation
    }


    fun sendRequestToJoin(
        reservation: ReservationDTO
    ) {
        mainScope.launch {
            reservationRepository.sendRequestToJoin(reservation)
        }
    }

    fun getPublicGames(
        date: LocalDate,
        sports: Sports,
        onSuccess: (List<ReservationDTO>) -> Unit,
        onFailure: () -> Unit
    ) {
        mainScope.launch {
            reservationRepository.getPublicGames(date, sports.toString().uppercase(), onFailure, onSuccess)
        }

    }

    private fun initPendingFriends(reservation: ReservationDTO) {
        oldPendingFriends = _pendingFriends.value!!
        _pendingFriends.value = reservation.pendings.map { Pair(it, false) }
    }

    private fun initAcceptedFriends(reservation: ReservationDTO) {
        oldAcceptedFriends = _acceptedFriends.value!!
        _acceptedFriends.value = reservation.accepted.map { Pair(it, false) }
    }

    fun deleteReservation(
        reservationDTO: ReservationDTO,
        date: LocalDate,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        loading.value = true
        reservationRepository.delete(reservationDTO, date, onFailure) {
            onSuccess()
        }

    }

    private fun initNotInvitedFriends(): LiveData<List<Pair<Profile, Boolean>>> {
        currentReservation.observeForever {
            if (it != null) {
                val acceptedEmails = it.accepted.map { p -> p.email }
                val pendingEmail = it.pendings.map { p -> p.email }
                oldNotInvitedFriends = _notInvitedFriends.value!!
                _notInvitedFriends.value = profile.friends.filter { p ->
                    !pendingEmail.contains(p.email) && !acceptedEmails.contains(p.email)
                }.map { p -> Pair(p, false) }

            }
        }
        return notInvitedFriends
    }


    fun updateNotInvitedFriends(profile: Profile) {
        oldNotInvitedFriends = _notInvitedFriends.value!!
        _notInvitedFriends.value = notInvitedFriends.value?.map {
            if (it.first.email == profile.email) {
                Pair(it.first, !it.second)
            } else {
                it
            }
        }
    }

    fun updateAcceptedFriends(profile: Profile) {
        oldAcceptedFriends = _acceptedFriends.value!!
        _acceptedFriends.value = acceptedFriends.value?.map {
            if (it.first.email == profile.email) {
                Pair(it.first, !it.second)
            } else {
                it
            }
        }
    }

    fun updatePendingFriends(profile: Profile) {
        oldPendingFriends = _pendingFriends.value!!
        _pendingFriends.value = _pendingFriends.value?.map {
            if (it.first.email == profile.email) {
                Pair(it.first, !it.second)
            } else {
                it
            }
        }
    }


}