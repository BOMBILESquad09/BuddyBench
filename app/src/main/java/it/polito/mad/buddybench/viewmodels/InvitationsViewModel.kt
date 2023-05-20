package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.InvitationsRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class InvitationsViewModel @Inject constructor() : ViewModel() {

    val invitationsRepository = InvitationsRepository()

    private val _invitations: MutableLiveData<List<ReservationDTO>> = MutableLiveData(listOf())
    val invitations: LiveData<List<ReservationDTO>> = _invitations

    private val invitationSize: MutableLiveData<Int> = MutableLiveData(0)

    fun subscribeInvitations(): LiveData<List<ReservationDTO>>{
        invitationsRepository.subscribeInvitations( onSuccess = {
            invitationSize.postValue(it)
        })
        invitationSize.observeForever{
            if(it != 0)
                runBlocking {
                    _invitations.postValue(invitationsRepository.getInvitations())
                }
        }
        return invitations
    }

    fun acceptInvitation(reservationDTO: ReservationDTO){
        runBlocking {
            invitationsRepository.acceptInvitation(reservationDTO)
        }
    }

    fun refuseInvitation(reservationDTO: ReservationDTO){
        runBlocking {
            invitationsRepository.refuseInvitation(reservationDTO)
        }
    }

    fun sendInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.sendInvitations(reservationDTO, invitedUsers)
            onSuccess()
        }
    }

    fun removeInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.removeInvitations(reservationDTO, invitedUsers)
            onSuccess()
        }
    }

    fun removeAcceptedInvitations(reservationDTO: ReservationDTO, invitedUsers: List<String>, onSuccess: () -> Unit){
        runBlocking {
            invitationsRepository.removeAcceptedInvitations(reservationDTO, invitedUsers)
            onSuccess()
        }
    }






}