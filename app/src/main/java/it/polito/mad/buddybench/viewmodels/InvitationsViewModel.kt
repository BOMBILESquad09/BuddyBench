package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.InvitationsRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor() : ViewModel() {

    private val invitationsRepository = InvitationsRepository()
    private val _loading: MutableLiveData<Boolean> = MutableLiveData(null)
    val loading: LiveData<Boolean> = _loading
    private val _invitations: MutableLiveData<List<ReservationDTO>> = MutableLiveData(listOf())
    val invitations: LiveData<List<ReservationDTO>> = _invitations

    private val invitationSize: MutableLiveData<Int> = MutableLiveData(0)
    private val mainScope = viewModelScope
    var onFailure = {}


    lateinit var popNotification: (ReservationDTO) -> Unit
    fun subscribeInvitations( onSuccess: (Int) -> Unit): LiveData<List<ReservationDTO>>{
        invitationsRepository.subscribeInvitations( onFailure = onFailure, onSuccess = {
            _loading.postValue(!invitationsRepository.subscribed)
            invitationsRepository.subscribed = true
            getAll(onSuccess)
            invitationSize.postValue(it)
        })

        return invitations
    }

    fun getAll(onSuccess : (Int) -> Unit = {}){
        mainScope.launch {

            var freshInvitations = invitationsRepository.getInvitations(onFailure)
            freshInvitations = freshInvitations.filter { LocalDateTime.now() < LocalDateTime.of(it.date, it.startTime) }
            if(_invitations.value!!.size < freshInvitations.size){

                val freshInvitationsId = freshInvitations.map {it.id}
                val oldInvitationsId = _invitations.value!!.map { it.id }
                val newInvitations = freshInvitationsId.filter {
                    !oldInvitationsId.contains(it)
                }
                newInvitations.forEach {
                    popNotification(freshInvitations.find { fi -> it == fi.id }!!)
                }
            }
            onSuccess(freshInvitations.size)
            _invitations.postValue(freshInvitations)
            if(_loading.value == true)
                _loading.postValue(false)
        }
    }

    fun acceptInvitation(reservationDTO: ReservationDTO, onSuccess: () -> Unit){
        mainScope.launch {
            invitationsRepository.acceptInvitation(reservationDTO, onFailure, onSuccess)
        }
    }

    fun refuseInvitation(reservationDTO: ReservationDTO, onSuccess: () -> Unit){
        invitationsRepository.refuseInvitation(reservationDTO, onFailure, onSuccess)
    }

    fun sendInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
            invitationsRepository.sendInvitations(reservationDTO, invitedUsers, onFailure, onSuccess)

    }

    fun removeInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
            invitationsRepository.removeInvitations(reservationDTO, invitedUsers, onFailure,onSuccess)


    }

    fun removeAcceptedInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
            invitationsRepository.removeAcceptedInvitations(reservationDTO, invitedUsers, onFailure, onSuccess)

    }






}