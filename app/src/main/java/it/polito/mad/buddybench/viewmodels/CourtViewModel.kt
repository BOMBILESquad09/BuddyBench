package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.buddybench.entities.Court
import it.polito.mad.buddybench.enums.Sports

class CourtViewModel: ViewModel() {

    private val _initial_value = Court(address = "Via Roma 16, Torino", courtName = "Tennis Club", feeHour = 12, sport = 0)
    private val _court: MutableLiveData<Court> = MutableLiveData()

    /**
     * Make a reservation for a court
     * Possible actions ->
     */

}