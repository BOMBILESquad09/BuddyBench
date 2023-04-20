package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.toEntity
import it.polito.mad.buddybench.entities.Court

class CourtViewModel: ViewModel() {

    private val _initialValue = Court(address = "Via Roma 16, Torino", courtName = "Tennis Club", feeHour = 12, sport = 0)
    private val _court: MutableLiveData<Court> = MutableLiveData(_initialValue)

    // ** Expose court to other classes (view)
    val court: LiveData<Court> get() = _court

    /**
     * Make a reservation for a court
     * Possible actions
     */
    fun getMockCourt(): LiveData<Court> {
        // ** Mock DB
        val court = CourtDTO(
            sport = 0,
            courtName = "Tennis Club #2",
            feeHour = 15,
            address = "Via Roma 19, Torino"
        ).toEntity()

        _court.value = court
        return _court
    }
}