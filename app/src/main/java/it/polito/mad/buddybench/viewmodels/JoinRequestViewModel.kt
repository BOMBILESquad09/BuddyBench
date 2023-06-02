package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.ReservationRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinRequestViewModel @Inject constructor() : ViewModel() {

    private val _currentReservation: MutableLiveData<ReservationDTO> = MutableLiveData(null)
    val currentReservation: MutableLiveData<ReservationDTO> = _currentReservation

    var listenerRegistration: ListenerRegistration? = null

    private val reservationRepository = ReservationRepository()

    fun getUpdatedReservation(reservation: ReservationDTO) {
        viewModelScope.launch {
            listenerRegistration = reservationRepository.getUpdatedReservation(
                reservation.id,
                {}
            ) {
                currentReservation.postValue(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration!!.remove()
    }

    fun confirmRequest(email: String) {
        viewModelScope.launch {
            reservationRepository.acceptJointRequest(
                _currentReservation.value!!,
                email,
                {},
                {
                    getUpdatedReservation(_currentReservation.value!!)
                }
            )
        }
    }

    fun rejectRequest(email: String) {
        viewModelScope.launch {
            reservationRepository.rejectJointRequest(
                _currentReservation.value!!,
                email,
                {},
                {
                    getUpdatedReservation(_currentReservation.value!!)
                }
            )
        }
    }


}