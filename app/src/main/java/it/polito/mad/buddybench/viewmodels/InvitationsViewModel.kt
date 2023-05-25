package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val invitationsRepository = InvitationsRepository()

    private val _invitations: MutableLiveData<List<ReservationDTO>> = MutableLiveData(listOf())
    val invitations: LiveData<List<ReservationDTO>> = _invitations

    private val invitationSize: MutableLiveData<Int> = MutableLiveData(0)
    val mainScope = MainScope()
    var onFailure = {}
    fun subscribeInvitations(onSuccess: (Int) -> Unit): LiveData<List<ReservationDTO>>{
        invitationsRepository.subscribeInvitations( onFailure = onFailure, onSuccess = {
            onSuccess(it)
            invitationSize.postValue(it)
        })
        invitationSize.observeForever{
            if(it != null && it != 0)
                getAll()
            else{
                _invitations.postValue(listOf())
            }
        }
        println("diocaneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
        return invitations
    }

    fun getAll(){
        mainScope.launch {
            _invitations.postValue(invitationsRepository.getInvitations(onFailure))
        }
    }

    fun acceptInvitation(reservationDTO: ReservationDTO){
        runBlocking {
            invitationsRepository.acceptInvitation(reservationDTO, onFailure)
        }
    }

    fun refuseInvitation(reservationDTO: ReservationDTO){
        runBlocking {
            invitationsRepository.refuseInvitation(reservationDTO, onFailure)
        }
    }

    fun sendInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.sendInvitations(reservationDTO, invitedUsers, onFailure)
            onSuccess()
        }
    }

    fun removeInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.removeInvitations(reservationDTO, invitedUsers, onFailure)
            onSuccess()
        }
    }

    fun removeAcceptedInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.removeAcceptedInvitations(reservationDTO, invitedUsers, onFailure)
            onSuccess()
        }
    }






}