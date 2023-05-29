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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class InvitationsRepository {
    val db = FirebaseFirestore.getInstance()
    private val reservationRepository = ReservationRepository()
    var subscribed: Boolean = false

    fun subscribeInvitations(onFailure: () -> Unit, onSuccess: (Int) -> Unit) {
        if(subscribed) return
        val currentEmail = Firebase.auth.currentUser!!.email!!
        db.collection("users").document(currentEmail).addSnapshotListener { value, error ->
            if (error == null && value != null && value.exists()) {
                onSuccess((value!!.data!!["play_request_pendings"] as List<DocumentReference>).size)
            } else {
                onFailure()
            }
        }
        subscribed = true
    }


    suspend fun getInvitations(onFailure: () -> Unit): List<ReservationDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val invitations = (db.collection("users").document(currentEmail)
                    .get().await().data!!["play_request_pendings"] as List<DocumentReference>).map {
                    reservationRepository.getReservation(it.id, userOrganizer = true, onFailure)
                }.filterNotNull()


                return@withContext invitations.filter { LocalDateTime.of(it.date, it.startTime) > LocalDateTime.now() }
            } catch (_: Exception) {
                onFailure()
            }
            listOf()
        }
    }

    suspend fun sendInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val reservationDocName = reservationDTO.id
                val invitationsResponse = db
                    .collection("reservations")
                    .document(reservationDocName)
                    .update(
                        "pendings",
                        FieldValue.arrayUnion(*usersToInvite.map { u -> db.document("users/$u") }
                            .toTypedArray())
                    )
                usersToInvite.map {
                    db.collection("users").document(it)
                        .update(
                            "play_request_pendings",
                            FieldValue.arrayUnion(db.document("reservations/${reservationDTO.id}"))
                        )
                }.forEach { it.await() }
                invitationsResponse.await()
                onSuccess()
            } catch (_: Exception) {
                onFailure()
            }
        }
    }

    suspend fun removeInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val reservationDocName = reservationDTO.id
                val invitationsResponse = db
                    .collection("reservations")
                    .document(reservationDocName)
                    .update(
                        mapOf(
                            "pendings" to FieldValue.arrayRemove(*usersToInvite.map { u ->
                                db.document(
                                    "users/$u"
                                )
                            }.toTypedArray()),
                            "accepted" to FieldValue.arrayRemove(*usersToInvite.map { u ->
                                db.document(
                                    "users/$u"
                                )
                            }.toTypedArray())
                        )
                    )
                usersToInvite.map {
                    db.collection("users").document(it)
                        .update(
                            "play_request_pendings",
                            FieldValue.arrayRemove(db.document("reservations/${reservationDTO.id}"))
                        )
                }.forEach { it.await() }
                invitationsResponse.await()
                onSuccess()
            } catch (_: Exception) {
                onFailure()
            }


        }
    }

    suspend fun removeAcceptedInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val reservationDocName = reservationDTO.id
                val invitationsResponse = db
                    .collection("reservations")
                    .document(reservationDocName)
                    .update(
                        "accepted",
                        FieldValue.arrayRemove(*usersToInvite.map { u -> db.document("users/$u") }
                            .toTypedArray())
                    )
                invitationsResponse.await()
                onSuccess()
            } catch (_: Exception) {
                onFailure()
            }


        }
    }

    suspend fun acceptInvitation(reservationDTO: ReservationDTO, onFailure: () -> Unit, onSuccess: () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmailDoc =
                    db.collection("users").document(Firebase.auth.currentUser!!.email!!)
                val reservationDoc = db.collection("reservations").document(reservationDTO.id)
                val firstResponse = reservationDoc
                    .update(
                        mapOf(
                            "pendings" to FieldValue.arrayRemove(currentEmailDoc),
                            "accepted" to FieldValue.arrayUnion(currentEmailDoc)
                        )
                    )
                val secondResponse = currentEmailDoc
                    .update("play_request_pendings", FieldValue.arrayRemove(reservationDoc))
                firstResponse.await()
                secondResponse.await()
                onSuccess()
            } catch (_: Exception) {
                onFailure()
            }

        }
    }

    suspend fun refuseInvitation(reservationDTO: ReservationDTO, onFailure: () -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val currentEmailDoc =
                    db.collection("users").document(Firebase.auth.currentUser!!.email!!)
                val reservationDoc = db.collection("reservations").document(reservationDTO.id)
                val firstResponse = reservationDoc
                    .update(
                        mapOf(
                            "pendings" to FieldValue.arrayRemove(currentEmailDoc)
                        )
                    )
                val secondResponse = currentEmailDoc
                    .update("play_request_pendings", FieldValue.arrayRemove(reservationDoc))
                firstResponse.await()
                secondResponse.await()
            } catch (_: Exception) {
                onFailure()
            }

        }
    }


}