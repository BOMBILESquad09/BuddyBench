package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.activities.court.DialogSheetDeleteReservation
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.TimeSlotsNotAvailableException
import it.polito.mad.buddybench.persistence.dto.ReservationDTO

import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.firebaseRepositories.ReservationRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
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
    val loading = MutableLiveData(false)

    var refresh: Boolean = false
    var email: String = ""

    lateinit var  profile: Profile


    val reservationRepositoryFirebase = ReservationRepository()

    // ** Expose to other classes (view)
    val reservations: LiveData<HashMap<LocalDate, List<ReservationDTO>>> get() = _reservations

    private val _pendingFriends: MutableLiveData<List<Pair<Profile, Boolean>>> = MutableLiveData(listOf())
    var oldPendingFriends: List<Pair<Profile, Boolean>> = _pendingFriends.value!!
    val pendingFriends: LiveData<List<Pair<Profile, Boolean>>> = _pendingFriends

    private val _acceptedFriends: MutableLiveData<List<Pair<Profile, Boolean>>> = MutableLiveData(listOf())
    var oldAcceptedFriends: List<Pair<Profile, Boolean>> = _acceptedFriends.value!!
    val acceptedFriends: LiveData<List<Pair<Profile, Boolean>>> = _acceptedFriends

    private val _notInvitedFriends: MutableLiveData<List<Pair<Profile, Boolean>>> = MutableLiveData(listOf())
    var oldNotInvitedFriends: List<Pair<Profile, Boolean>> = _notInvitedFriends.value!!
    val notInvitedFriends: LiveData<List<Pair<Profile, Boolean>>> = _notInvitedFriends



    fun getAllByUser(): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        loading.value = (true)
        runBlocking {
            val reservations = reservationRepositoryFirebase.getAllByUser()
            _reservations.postValue(reservations)
            loading.postValue(false)
        }


        return _reservations
    }

    fun saveReservation(
        reservation: ReservationDTO,
        edit: Boolean,
        oldDate: LocalDate?,
        failureCallback: () -> Unit
    ) {
        loading.value = true
        runBlocking {
            try{
                if (!edit) {
                    reservationRepositoryFirebase.save(reservation)
                    loading.postValue(false)

                } else {
                    reservationRepositoryFirebase.update(
                        reservation,
                        oldDate!!)
                    loading.postValue(false)

                }
            } catch (tex: TimeSlotsNotAvailableException){
                failureCallback()
            }
        }
    }


    fun updateSelectedDay(date: LocalDate) {
        oldDate = _selectedDate.value
        _selectedDate.value = date
        //getAllByUser()
    }

    fun getSelectedReservations(): List<ReservationDTO>? {
        return reservations.value?.get(selectedDate.value ?: LocalDate.now())
    }

    fun getReservation(
        reservationID: String,
    ): MutableLiveData<ReservationDTO?> {
        runBlocking {
            val res = reservationRepositoryFirebase.getReservation(reservationID)
            _currentReservation.postValue(res)
            initAcceptedFriends(res)
            initPendingFriends(res)
            initNotInvitedFriends()

        }
        return _currentReservation
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
        dialogSheetDeleteReservation: DialogSheetDeleteReservation,
    ) {
        //da chiamare nella view NON QUI
        dialogSheetDeleteReservation.isCancelable = false
        loading.postValue(true)
        runBlocking {
            reservationRepositoryFirebase.delete(reservationDTO, date)
            loading.postValue(false)

        }
    }

    private fun initNotInvitedFriends(): LiveData<List<Pair<Profile,Boolean>>>{
        currentReservation.observeForever {
            if (it != null){
                val acceptedEmails = it.accepted.map { p ->p.email }
                val pendingEmail = it.pendings.map { p->p.email }
                oldNotInvitedFriends = _notInvitedFriends.value!!
                _notInvitedFriends.value =  profile.friends.filter { p -> !pendingEmail.contains(p.email) && !acceptedEmails.contains(p.email) }.map { p->Pair(p, false) }

            }
        }
        return notInvitedFriends
    }



    fun updateNotInvitedFriends(profile: Profile){
        oldNotInvitedFriends = _notInvitedFriends.value!!
        _notInvitedFriends.value = notInvitedFriends.value?.map {
            if(it.first.email == profile.email){
                Pair(it.first, !it.second)
            }else{
                it
            }
        }
    }

    fun updateAcceptedFriends(profile: Profile){
        oldAcceptedFriends = _acceptedFriends.value!!
        _acceptedFriends.value = acceptedFriends.value?.map {
            if(it.first.email == profile.email){
                Pair(it.first, !it.second)
            }else{
                it
            }
        }
    }

    fun updatePendingFriends(profile: Profile){
        oldPendingFriends = _pendingFriends.value!!
        _pendingFriends.value = _pendingFriends.value?.map {
            if(it.first.email == profile.email){
                Pair(it.first, !it.second)
            }else{
                it
            }
        }
    }




}