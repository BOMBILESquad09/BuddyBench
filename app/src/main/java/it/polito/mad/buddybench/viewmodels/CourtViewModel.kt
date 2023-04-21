package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.buddybench.dao.CourtDao
import it.polito.mad.buddybench.dto.CourtDTO
import it.polito.mad.buddybench.dto.toEntity
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate

class CourtViewModel: ViewModel() {

    private val _initialValue = Court(address = "Via Roma 16, Torino", courtName = "Tennis Club", feeHour = 12, sport = 0)
    private val _court: MutableLiveData<Court> = MutableLiveData(_initialValue)

    // ** DateTime pickers

    // Range of days between 2 weeks
    // Range of hours between 8:00 to 23:00 TODO: Include opening hours, and exclude unavailable hours
    private val _days = Utils.generateDateRange(LocalDate.now(), LocalDate.now().plusDays(14))
    private val _selectedDay: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    // ** Expose to other classes (view)
    val court: LiveData<Court> get() = _court
    val days: List<LocalDate> get() = _days
    val selectedDay: LiveData<LocalDate> get() = _selectedDay

    /**
     * Get Court
     * @return Court LiveData
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

    /**
     * Select day from the list
     * Update UI in the view
     */
    fun selectDay(date: LocalDate): LiveData<LocalDate> {
        _selectedDay.value = date
        return _selectedDay
    }

}