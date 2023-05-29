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
            _loading.postValue(true)
            onSuccess(it)
            invitationSize.postValue(it)
        })
        invitationSize.observeForever{
            if(it != null && it != 0)
                getAll(true)
            else{
                _loading.postValue(false)
                _invitations.postValue(listOf())
            }
        }
        return invitations
    }

    fun getAll(init: Boolean = false){
        mainScope.launch {

            val freshInvitations = invitationsRepository.getInvitations(onFailure)
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

    fun refuseInvitation(reservationDTO: ReservationDTO){
        mainScope.launch{
            invitationsRepository.refuseInvitation(reservationDTO, onFailure)
        }
    }

    fun sendInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        mainScope.launch {
            invitationsRepository.sendInvitations(reservationDTO, invitedUsers, onFailure, onSuccess)
        }
    }

    fun removeInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        mainScope.launch {
            invitationsRepository.removeInvitations(reservationDTO, invitedUsers, onFailure,onSuccess)

        }
    }

    fun removeAcceptedInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        mainScope.launch {
            invitationsRepository.removeAcceptedInvitations(reservationDTO, invitedUsers, onFailure, onSuccess)
        }
    }






}