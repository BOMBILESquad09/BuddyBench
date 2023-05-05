package it.polito.mad.buddybench.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.persistence.dto.ReservationDTO

import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.repositories.ReservationRepository
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
    var email:String = ""

    @Inject
    lateinit var reservationRepository: ReservationRepository

    // ** Expose to other classes (view)
    val reservations: LiveData<HashMap<LocalDate, List<ReservationDTO>>> get() = _reservations

    fun getAll(): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        // Repository Call, All the repos return DTO Obj
        Thread {
            val reservations = reservationRepository.getAll()
            _reservations.postValue(reservations)
        }.start()

        return _reservations
    }

    fun getAllByUser(): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        Thread {
            loading.postValue(true)
            val reservations = reservationRepository.getAllByUser(email)
            println("------------------")

            _reservations.postValue(reservations)

        }.start()
        return _reservations
    }

    fun saveReservation(
        reservation: ReservationDTO,
        edit: Boolean,
        oldDate: LocalDate?,
        oldStartTime: LocalTime?
    ) {
        /*In a thread or not?*/



        if (edit)
            reservationRepository.update(reservation,oldDate!!, oldStartTime!!.hour)
        else
            reservationRepository.save(reservation)
    }


    fun updateSelectedDay(date: LocalDate) {
        oldDate = _selectedDate.value
        _selectedDate.value = date
        getAllByUser()
    }

    fun getSelectedReservations(): List<ReservationDTO>? {
        return reservations.value?.get(selectedDate.value ?: LocalDate.now())
    }

    fun getReservation(courtName: String, sport: Sports, email: String, date: LocalDate, startTime: Int): MutableLiveData<ReservationDTO?> {
        Thread {
            val sportName = Sports.toJSON(sport)
            val reservationDTO =
                reservationRepository.getReservation(
                    courtName,
                    sportName,
                    email,
                    date,
                    startTime
                )
            _currentReservation.postValue(reservationDTO)
        }.start()
        return _currentReservation
    }

    fun deleteReservation(courtName: String, sport: Sports, startTime: LocalTime, date: LocalDate, email: String) {
        reservationRepository.delete(
            courtName,
            sport,
            startTime,
            email,
            date
        )
    }



}