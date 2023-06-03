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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class JoinRequestViewModel @Inject constructor() : ViewModel() {

    val currentReservation: MutableLiveData<ReservationDTO> = MutableLiveData(null)
    var oldRequestJointList: List<Profile> = listOf()

    var listenerRegistration: ListenerRegistration? = null
    val mainScope = MainScope()

    private val reservationRepository = ReservationRepository()

    fun getUpdatedReservation(reservation: ReservationDTO) {
        runBlocking {
            reservationRepository.getUpdatedReservation(
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
        reservationRepository.acceptJointRequest(
            currentReservation.value!!,
            email,
            {},
            {
                oldRequestJointList = currentReservation.value!!.requests
//                getUpdatedReservation(currentReservation.value!!)
            }
        )

    }

    fun rejectRequest(email: String) {
        reservationRepository.rejectJointRequest(
            currentReservation.value!!,
            email,
            {},
            {
                oldRequestJointList = currentReservation.value!!.requests
//                getUpdatedReservation(currentReservation.value!!)
            }
        )

    }


}