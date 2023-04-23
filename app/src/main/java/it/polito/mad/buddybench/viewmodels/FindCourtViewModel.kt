package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.dto.ReservationDTO
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.repositories.SportRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SportsSelectionViewModel @Inject constructor(): ViewModel() {
    private var _sports: MutableLiveData<List<Sports>> = MutableLiveData(listOf())
    var sports: LiveData<List<Sports>> = _sports
    var selectedSport: MutableLiveData<Sports> = MutableLiveData(Sports.FOOTBALL)


    @Inject
    lateinit var sportRepository: SportRepository

    fun getAll(): LiveData<List<Sports>>{
        _sports.value = sportRepository.getAll().map { Sports.valueOf(it.name) }
        return sports
    }
}