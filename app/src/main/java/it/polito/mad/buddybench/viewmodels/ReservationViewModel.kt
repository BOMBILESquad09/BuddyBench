package it.polito.mad.buddybench.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.entities.Reservation
import it.polito.mad.buddybench.entities.toReservationDTO
import it.polito.mad.buddybench.repositories.CourtRepository
import it.polito.mad.buddybench.repositories.ReservationRepository
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(): ViewModel() {

    private val _reservations: MutableLiveData<HashMap<LocalDate, List<ReservationDTO>>> = MutableLiveData(null)
    val reservation: LiveData<HashMap<LocalDate, List<ReservationDTO>>> = _reservations


    @Inject
    lateinit var reservationRepository: ReservationRepository

    // ** Expose to other classes (view)
    val reservations: LiveData<HashMap<LocalDate, List<ReservationDTO>>> get() = _reservations

    fun getAll(): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        // Repository Call, All the repos return DTO Obj
        val reservations = reservationRepository.getAll()
        _reservations.value = reservations
        return _reservations
    }

    fun getAllByUser(email: String): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        // Repository Call, All the repos return DTO Obj
        val reservations = reservationRepository.getAllByUser(email)
        println("Reservation For: $email")
        println(reservations)
        _reservations.value = reservations
        return _reservations
    }

    fun saveReservation(
        reservation: ReservationDTO
    ) {
        reservationRepository.save(
            reservation
        )
    }



}