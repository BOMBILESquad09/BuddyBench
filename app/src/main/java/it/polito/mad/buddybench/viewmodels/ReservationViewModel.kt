package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.activities.court.DialogSheetDeleteReservation
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
    var email: String = ""

    @Inject
    lateinit var reservationRepository: ReservationRepository

    val reservationRepositoryFirebase = it.polito.mad.buddybench.persistence.firebaseRepositories.ReservationRepository()

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
        loading.value = (true)
        reservationRepositoryFirebase.getAllByUser(email){
            val reservations = it
            _reservations.postValue(reservations)
            loading.postValue(false)
        }
        /*
        Thread {
            loading.postValue(true)
            val reservations = reservationRepository.getAllByUser(email)
            println("------------------")

            _reservations.postValue(reservations)

        }.start()*/
        return _reservations
    }

    fun saveReservation(
        reservation: ReservationDTO,
        edit: Boolean,
        oldDate: LocalDate?,
        oldStartTime: LocalTime?,
    ) {
        loading.postValue(true)
        if(!edit){
            reservationRepositoryFirebase.save(reservation){
                loading.postValue(false)
            }
        }

        else{
           reservationRepositoryFirebase.update(reservation, oldDate!!, oldStartTime!!.hour){
               loading.postValue(false)
           }
        }
        /*if (edit) {
            Thread {
                loading.postValue(true)
                reservationRepository.update(reservation, oldDate!!, oldStartTime!!.hour)
                loading.postValue(false)
            }.start()
        }
        else
            Thread {
                loading.postValue(true)
                reservationRepository.save(reservation)
                loading.postValue(false)
            }.start()*/
    }


    fun updateSelectedDay(date: LocalDate) {
        oldDate = _selectedDate.value
        _selectedDate.value = date
        getAllByUser()
    }

    fun getSelectedReservations(): List<ReservationDTO>? {
        return reservations.value?.get(selectedDate.value ?: LocalDate.now())
    }

    fun getReservation(
        courtName: String,
        sport: Sports,
        email: String,
        date: LocalDate,
        startTime: Int
    ): MutableLiveData<ReservationDTO?> {
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

    fun deleteReservation(
        courtName: String,
        sport: Sports,
        startTime: LocalTime,
        date: LocalDate,
        email: String,
        dialogSheetDeleteReservation: DialogSheetDeleteReservation,
    ) {
        //da chiamare nella view NON QUI
        dialogSheetDeleteReservation.isCancelable = false
        loading.postValue(true)
        reservationRepositoryFirebase.delete(courtName, sport, startTime, email, date){
            loading.postValue(false)
        }


    }


}