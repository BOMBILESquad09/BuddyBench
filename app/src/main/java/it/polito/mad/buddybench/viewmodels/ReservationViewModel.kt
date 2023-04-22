package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.entities.Reservation
import it.polito.mad.buddybench.repositories.CourtRepository
import it.polito.mad.buddybench.repositories.ReservationRepository
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(): ViewModel() {

    private var _initialValue: HashMap<LocalDate, List<ReservationDTO>>? = null
    private val _reservations: MutableLiveData<HashMap<LocalDate, List<ReservationDTO>>> = MutableLiveData(_initialValue)

    @Inject
    lateinit var reservationRepository: ReservationRepository
    // ** Expose to other classes (view)
    val reservations: LiveData<HashMap<LocalDate, List<ReservationDTO>>> get() = _reservations

    fun getAll(): LiveData<HashMap<LocalDate, List<ReservationDTO>>> {
        // Repository Call, All the repos return DTO Obj
        val reservations = reservationRepository.getAll()

        // ** TODO: Filter here the right time slots from the opening hours and availability

        _reservations.value = reservations
        return _reservations
    }


}