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

    fun subscribeInvitations(onFailure: () -> Unit, onSuccess: () -> Unit) {
        if (subscribed) return
        val currentEmail = Firebase.auth.currentUser!!.email!!
        db.collection("users").document(currentEmail).addSnapshotListener { value, error ->
            if (error == null && value != null && value.exists()) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }


    suspend fun getInvitations(onFailure: () -> Unit): List<ReservationDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val invitations = (db.collection("users").document(currentEmail)
                    .get().await().data!!["play_request_pendings"] as List<DocumentReference>).map {
                    reservationRepository.getReservation(it.id, onFailure)
                }.filterNotNull()

                return@withContext invitations.filter {
                    LocalDateTime.of(
                        it.date,
                        it.startTime
                    ) > LocalDateTime.now()
                }
            } catch (e: Exception) {
                println(e)
                onFailure()
            }
            listOf()
        }
    }

    fun sendInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        db.runTransaction { t ->
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            val reservationDoc = db
                .collection("reservations")
                .document(reservationDocName)
            t.update(reservationDoc,
                "pendings",
                FieldValue.arrayUnion(*usersToInvite.map { u -> db.document("users/$u") }
                    .toTypedArray())
            )
            usersToInvite.map {
                t.update(
                    db.collection("users").document(it),
                    "play_request_pendings",
                    FieldValue.arrayUnion(db.document("reservations/${reservationDTO.id}"))
                )
            }
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun removeInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        db.runTransaction { t ->

            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            val invitationDoc = db
                .collection("reservations")
                .document(reservationDocName)
            t.update(
                invitationDoc,
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
                t.update(
                    db.collection("users").document(it),
                    "play_request_pendings",
                    FieldValue.arrayRemove(db.document("reservations/${reservationDTO.id}"))
                )
            }
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }


    fun removeAcceptedInvitations(
        reservationDTO: ReservationDTO,
        usersToInvite: List<String>,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {
        db.runTransaction { t ->
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val reservationDocName = reservationDTO.id
            val invitationsDoc = db
                .collection("reservations")
                .document(reservationDocName)
            t.update(invitationsDoc,
                "accepted",
                FieldValue.arrayRemove(*usersToInvite.map { u -> db.document("users/$u") }
                    .toTypedArray())
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun acceptInvitation(
        reservationDTO: ReservationDTO,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->
            val currentEmailDoc =
                db.collection("users").document(Firebase.auth.currentUser!!.email!!)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            t
                .update(
                    reservationDoc,
                    mapOf(
                        "pendings" to FieldValue.arrayRemove(currentEmailDoc),
                        "accepted" to FieldValue.arrayUnion(currentEmailDoc)
                    )
                )
            t.update(
                currentEmailDoc,
                "play_request_pendings",
                FieldValue.arrayRemove(reservationDoc)
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun refuseInvitation(reservationDTO: ReservationDTO, onFailure: () -> Unit, onSuccess: () -> Unit) {
        db.runTransaction { t ->

            val currentEmailDoc =
                db.collection("users").document(Firebase.auth.currentUser!!.email!!)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            t
                .update(
                    reservationDoc,
                    mapOf(
                        "pendings" to FieldValue.arrayRemove(currentEmailDoc)
                    )
                )

            t.update(
                currentEmailDoc,
                "play_request_pendings",
                FieldValue.arrayRemove(reservationDoc)
            )

        }.addOnSuccessListener {
            onSuccess
        }.addOnFailureListener {
            onFailure()
        }

    }


}