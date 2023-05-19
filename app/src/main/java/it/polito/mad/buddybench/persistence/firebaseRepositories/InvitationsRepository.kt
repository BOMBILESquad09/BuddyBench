package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.repositories.ReservationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class InvitationsRepository {
    val db = FirebaseFirestore.getInstance()
    val reservationRepository = ReservationRepository()


    fun subscribeInvitations(onSuccess: (Int) -> Unit){
        val currentEmail = Firebase.auth.currentUser!!.email!!
        db.collection("users").document(currentEmail).addSnapshotListener { value, error ->
            if(error == null && value != null && value.exists()){
                onSuccess((value!!.data!!["play_request_pendings"] as List<DocumentReference>).size)
            }
        }
    }
    suspend fun getInvitations(): List<ReservationDTO>{
        return withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val invitations = (db.collection("users")
                .document(currentEmail)
                .get().await().data!!["play_request_pendings"] as List<DocumentReference>).map {
                    reservationRepository.getReservation(it.id, userOrganizer = true)
            }
            invitations
        }
    }
    suspend fun sendInvitations(reservationDTO: ReservationDTO,usersToInvite: List<String>){
        withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            val invitationsResponse = db
                .collection("reservations")
                .document(reservationDocName)
                .update("pendings", FieldValue.arrayUnion(*usersToInvite.map { u -> db.document("users/$u") }.toTypedArray()))
            usersToInvite.map {
                db.collection("users").document(it)
                    .update("play_request_pendings", FieldValue.arrayUnion(db.document("reservations/${reservationDTO.id}")))
            }.forEach { it.await() }
            invitationsResponse.await()

        }
    }

    suspend fun removeInvitations(reservationDTO: ReservationDTO,usersToInvite: List<String>){
        withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            val invitationsResponse = db
                .collection("reservations")
                .document(reservationDocName)
                .update("pendings", FieldValue.arrayRemove(*usersToInvite.map { u -> db.document("users/$u") }.toTypedArray()))
            usersToInvite.map {
                db.collection("users").document(it)
                    .update("play_request_pendings", FieldValue.arrayRemove(db.document("reservations/${reservationDTO.id}")))
            }.forEach { it.await() }
            invitationsResponse.await()

        }
    }

    suspend fun removeAcceptedInvitations(reservationDTO: ReservationDTO,usersToInvite: List<String>){
        withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            println("diocaneeeeeeeeeeeeeeee")
            val invitationsResponse = db
                .collection("reservations")
                .document(reservationDocName)
                .update("accepted", FieldValue.arrayRemove(*usersToInvite.map { u -> db.document("users/$u") }.toTypedArray()))
            invitationsResponse.await()

        }
    }

    suspend fun acceptInvitation(reservationDTO: ReservationDTO){
        withContext(Dispatchers.IO){
            val currentEmailDoc = db.collection("users").document(Firebase.auth.currentUser!!.email!!)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            val firstResponse = reservationDoc
                .update(mapOf(
                    "pendings" to FieldValue.arrayRemove(currentEmailDoc),
                    "accepted" to FieldValue.arrayUnion(currentEmailDoc)
                ))
            val secondResponse = currentEmailDoc
                .update("play_request_pendings", FieldValue.arrayRemove(reservationDoc))
            firstResponse.await()
            secondResponse.await()
        }
    }

    suspend fun refuseInvitation(reservationDTO: ReservationDTO){
        withContext(Dispatchers.IO){
            val currentEmailDoc = db.collection("users").document(Firebase.auth.currentUser!!.email!!)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            val firstResponse = reservationDoc
                .update(mapOf(
                    "pendings" to FieldValue.arrayRemove(currentEmailDoc)
                ))
            val secondResponse = currentEmailDoc
                .update("play_request_pendings", FieldValue.arrayRemove(reservationDoc))
            firstResponse.await()
            secondResponse.await()
        }
    }


}