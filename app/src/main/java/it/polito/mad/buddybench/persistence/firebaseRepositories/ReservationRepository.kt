package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.view.ScaleGestureDetector.OnScaleGestureListener
import androidx.compose.runtime.produceState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.TimeSlotsNotAvailableException
import it.polito.mad.buddybench.enums.Visibilities
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.dto.UserDTO

import it.polito.mad.buddybench.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationRepository {
    val db = FirebaseFirestore.getInstance()
    var subscribed = false

    fun subscribeReservations(onFailure: () -> Unit, onSuccess: () -> Unit){
        if (subscribed) return

        val currentEmail = Firebase.auth.currentUser!!.email!!
        db.collection("reservations")
            .whereEqualTo("user", db.document("users/$currentEmail"))
            .addSnapshotListener {
                    value, error ->
                if (error == null && value != null) {
                    onSuccess()
                } else {
                    onFailure()
                }

            }


    }





    suspend fun getAllByUser(
        onFailure: () -> Unit,
        onSuccess: (HashMap<LocalDate, List<ReservationDTO>>) -> Unit
    ) {
        return withContext(Dispatchers.IO) {
            try {
                val currentEmail = Firebase.auth.currentUser!!.email!!
                val myProfile = UserRepository.serializeUser(db.collection("users").document(currentEmail).get().await().data  as Map<String, Object>)
                val result = db.collection("reservations")
                    .whereEqualTo("user", db.document("users/$currentEmail")).get()
                val resultInvitedReservations = db.collection("reservations")
                    .whereArrayContains("accepted", db.document("users/$currentEmail")).get()

                val reservations = mutableListOf<ReservationDTO>()
                for (res in result.await()) {

                    val reservationDTO = ReservationDTO()
                    reservationDTO.date = LocalDate.parse(
                        res.data["date"] as String,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                    reservationDTO.startTime =
                        LocalTime.of((res.data["startTime"] as Long).toInt(), 0)
                    reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(), 0)
                    reservationDTO.equipment = res.data["equipment"] as Boolean
                    reservationDTO.id = res.data["id"] as String

                    val court = (res.data["court"] as DocumentReference).get().await()

                    val acceptedUsers =
                        (res.data["accepted"] as List<DocumentReference>).map { it.get() }
                            .map { it.await() }
                            .map { UserRepository.serializeUser(it.data as Map<String, Object>) }


                    val pendingUsers =
                        (res.data["pendings"] as List<DocumentReference>).map { it.get() }
                            .map { it.await() }
                            .map { UserRepository.serializeUser(it.data as Map<String, Object>) }

                    val requests =
                        (res.data["requests"] as List<DocumentReference>).map { it.get() }
                            .map { it.await() }
                            .map { UserRepository.serializeUser(it.data as Map<String, Object>) }

                    reservationDTO.court = court.toObject(CourtDTO::class.java)!!
                    reservationDTO.accepted = acceptedUsers
                    reservationDTO.pendings = pendingUsers
                    reservationDTO.requests = requests
                    reservationDTO.userOrganizer = myProfile
                    reservationDTO.visibility = Visibilities.fromStringToVisibility(res.data["visibilty"].toString())!!

                    reservations.add(reservationDTO)
                }
                for (res in resultInvitedReservations.await()) {
                    val reservationDTO = ReservationDTO()
                    reservationDTO.date = LocalDate.parse(
                        res.data["date"] as String,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                    reservationDTO.startTime =
                        LocalTime.of((res.data["startTime"] as Long).toInt(), 0)
                    reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(), 0)
                    reservationDTO.equipment = res.data["equipment"] as Boolean
                    reservationDTO.id = res.data["id"] as String
                    val court = (res.data["court"] as DocumentReference).get().await()
                    val acceptedUsers =
                        (res.data["accepted"] as List<DocumentReference>).map { it.get() }
                            .map { it.await() }
                            .map { UserRepository.serializeUser(it.data as Map<String, Object>) }
                            .filter { it.email == Firebase.auth.currentUser!!.email }
                    reservationDTO.court = court.toObject(CourtDTO::class.java)!!
                    reservationDTO.userOrganizer = UserRepository.serializeUser(
                        (res.data["user"] as DocumentReference).get()
                            .await().data!! as Map<String, Object>
                    )
                    reservationDTO.requests = listOf()

                    reservationDTO.accepted = acceptedUsers.plusElement(reservationDTO.userOrganizer)

                    reservationDTO.visibility = Visibilities.fromStringToVisibility(res.data["visibilty"].toString())!!

                    reservations.add(reservationDTO)
                }
                withContext(Dispatchers.Main){
                    onSuccess(ReservationDTO.toHashmap(reservations))

                }
            } catch (e: Exception) {
                println(e)
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }

        }
    }

    suspend fun save(
        reservationDTO: ReservationDTO,
        onFailure: () -> Unit,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {


        withContext(Dispatchers.IO) {
            try {
                withTimeout(10000) {
                    try {
                        val reservationMap: HashMap<String, Any> =
                            createReservationMap(reservationDTO)
                        val reservationID = reservationMap["id"] as String
                        val courtName =
                            reservationDTO.court.name.replace(
                                " ",
                                "_"
                            ) + "_" + reservationDTO.court.sport
                        val court = db.collection("courts")
                            .document(courtName)
                            .get().await()

                        val openingTime =
                            (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(
                                reservationDTO.date.dayOfWeek.name
                            )!!.get("openingTime") as Long
                        val closingTime =
                            (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(
                                reservationDTO.date.dayOfWeek.name
                            )!!.get("closingTime") as Long
                        val slots = (closingTime - openingTime).toInt()
                        val reservations = db.collection("reservations")
                            .whereEqualTo("court", db.document("courts/$courtName"))
                            .whereEqualTo("date", reservationDTO.date.toString())
                            .get().await()
                        var count = 0
                        for (r in reservations) {
                            val endTime = r.data["endTime"] as Long
                            val startTime = r.data["startTime"] as Long
                            if ((reservationDTO.startTime.hour in startTime until endTime)
                                || (reservationDTO.endTime.hour in (startTime + 1)..endTime)
                            ) {
                                throw TimeSlotsNotAvailableException()
                            }
                            count = (count + (endTime - startTime)).toInt()

                        }

                        db.runTransaction { t ->
                            val reservationDoc = db.collection("reservations")
                                .document(reservationID)
                            val uc = db.collection("unavailable_courts")
                                .document(reservationDTO.date.toString())

                            t.set(reservationDoc, reservationMap)
                            if ((count + (reservationDTO.endTime.hour - reservationDTO.startTime.hour)) == slots) {

                                t.set(
                                    uc,
                                    mapOf("courts" to FieldValue.arrayUnion(db.document("courts/$courtName"))),
                                    SetOptions.merge()
                                )
                            }

                        }.addOnSuccessListener {
                            onSuccess()
                        }.addOnFailureListener {
                            onFailure()
                        }
                    } catch (_: TimeSlotsNotAvailableException) {
                        withContext(Dispatchers.Main) {
                            onError()
                        }
                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) {
                            onFailure()
                        }
                    }
                }

            } catch (t: TimeoutCancellationException) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }


    }


    suspend fun update(
        reservationDTO: ReservationDTO,
        oldDate: LocalDate,
        onFailure: () -> Unit,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                withTimeout(10000) {
                    try {

                        val reservationMap = createReservationMap(reservationDTO)
                        reservationMap.remove("pendings")
                        reservationMap.remove("accepted")
                        val courtName =
                            reservationDTO.court.name.replace(
                                " ",
                                "_"
                            ) + "_" + reservationDTO.court.sport
                        val court = db.collection("courts").document(courtName).get().await()
                        val openingTime =
                            (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(
                                reservationDTO.date.dayOfWeek.name
                            )!!.get("openingTime") as Long
                        val closingTime =
                            (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(
                                reservationDTO.date.dayOfWeek.name
                            )!!.get("closingTime") as Long
                        val slots = (closingTime - openingTime).toInt()
                        val allReservation = db.collection("reservations")
                            .whereEqualTo("court", db.document("courts/$courtName"))
                            .whereEqualTo("date", reservationDTO.date.toString())
                            .get().await()
                        var count = 0
                        for (r in allReservation) {
                            val endTime = r.data["endTime"] as Long
                            val startTime = r.data["startTime"] as Long
                            if (reservationDTO.id == r.id) continue
                            if (
                                (reservationDTO.startTime.hour in startTime until endTime)
                                || (reservationDTO.endTime.hour in (startTime + 1)..endTime)
                            ) {
                                throw TimeSlotsNotAvailableException()
                            }
                            count = (count + (endTime - startTime)).toInt()
                        }

                        db.runTransaction { t ->
                            val reservationDoc = db.collection("reservations")
                                .document(reservationDTO.id)
                            t.update(reservationDoc, reservationMap)

                            val uc = db.collection("unavailable_courts")
                                .document(reservationDTO.date.toString())
                            if ((count + (reservationDTO.endTime.hour - reservationDTO.startTime.hour)) == slots) {
                                t.set(
                                    uc,
                                    mapOf("courts" to FieldValue.arrayUnion(db.document("courts/$courtName"))),
                                    SetOptions.merge()
                                )
                            } else {
                                t.set(
                                    uc,
                                    mapOf("courts" to FieldValue.arrayRemove(db.document("courts/$courtName")))
                                )
                            }

                        }.addOnSuccessListener {
                            onSuccess()

                        }.addOnFailureListener {

                            onFailure()
                        }

                    } catch (_: TimeSlotsNotAvailableException) {
                        withContext(Dispatchers.Main) {
                            onError()
                        }                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) {
                            onFailure()
                        }
                    }

                }
            } catch (t: TimeoutCancellationException) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }


    fun delete(
        reservationDTO: ReservationDTO,
        oldDate: LocalDate,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {


        val docCourtName =
            reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
        val reservationDoc = db.collection("reservations").document(reservationDTO.id)
        val ucDoc = db.collection("unavailable_courts").document(oldDate.toString())
        db.runTransaction {t ->
            t.delete(reservationDoc)
            t.update(ucDoc, "courts", FieldValue.arrayRemove(db.document("courts/$docCourtName")))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    suspend fun getReservation(
        reservationID: String,
        onFailure: () -> Unit
    ): ReservationDTO? {
        return withContext(Dispatchers.IO) {
            try {
                val res = db.collection("reservations").document(reservationID).get().await()
                withContext(Dispatchers.Default) {
                    mappingReservationFromDocument(res)
                }
            } catch (_: Exception) {
                onFailure()
                null
            }
        }
    }

    private fun createReservationMap(reservationDTO: ReservationDTO): HashMap<String, Any> {
        val courtName =
            reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
        val reservationID = if (reservationDTO.id != "") {
            reservationDTO.id
        } else {
            Utils.generateUUID()
        }
        val reservationMap: HashMap<String, Any> = HashMap()
        reservationMap["court"] = db.document("courts/$courtName")
        reservationMap["user"] = db.document("users/${Firebase.auth.currentUser!!.email}")
        reservationMap["date"] = reservationDTO.date.toString()
        reservationMap["startTime"] = reservationDTO.startTime.hour
        reservationMap["endTime"] = reservationDTO.endTime.hour
        reservationMap["equipment"] = reservationDTO.equipment
        reservationMap["accepted"] = listOf<DocumentReference>()
        reservationMap["pendings"] = listOf<DocumentReference>()
        reservationMap["requests"] = listOf<DocumentReference>()
        reservationMap["visibilty"] = reservationDTO.visibility
        reservationMap["id"] = reservationID

        return reservationMap
    }

    private suspend fun mappingReservationFromDocument(
        document: DocumentSnapshot,
    ): ReservationDTO {
        return withContext(Dispatchers.IO) {
            val reservationDTO = ReservationDTO()

            reservationDTO.date =
                LocalDate.parse(document.data!!["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
            reservationDTO.startTime =
                LocalTime.of((document.data!!["startTime"] as Long).toInt(), 0)
            reservationDTO.endTime = LocalTime.of((document.data!!["endTime"] as Long).toInt(), 0)
            reservationDTO.equipment = document.data!!["equipment"] as Boolean
            reservationDTO.id = document.data!!["id"] as String
            reservationDTO.userOrganizer = UserRepository.serializeUser(
                    (document.data!!["user"] as DocumentReference).get()
                        .await().data!! as Map<String, Object>
            )
            val acceptedUsers =
                (document.data!!["accepted"] as List<DocumentReference>).map { it.get() }
                    .map { it.await() }
                    .map { UserRepository.serializeUser(it.data as Map<String, Object>) }
            val pendingUsers =
                (document.data!!["pendings"] as List<DocumentReference>).map { it.get() }
                    .map { it.await() }
                    .map { UserRepository.serializeUser(it.data as Map<String, Object>) }

            //qui prendo chi mi richiede di joinare
            val requestingUsers = (document.data!!["requests"] as List<DocumentReference>)
                .map { it.get() }
                .map { it.await() }
                .map { UserRepository.serializeUser(it.data as Map<String, Object>) }

            reservationDTO.visibility = Visibilities.fromStringToVisibility(document.data!!["visibilty"].toString())!!
            reservationDTO.requests = requestingUsers
            reservationDTO.accepted = acceptedUsers
            if(reservationDTO.userOrganizer.email != Firebase.auth.currentUser!!.email!!)
                reservationDTO.accepted = acceptedUsers.plusElement(reservationDTO.userOrganizer).reversed()
            reservationDTO.pendings = pendingUsers
            val court = (document.data!!["court"] as DocumentReference).get().await()
            reservationDTO.court = court.toObject(CourtDTO::class.java)!!
            reservationDTO
        }
    }

    /* prendi tutte le reservation pubbliche o su richiesta per un certo giorno e per un certo sport*/
    suspend fun getPublicGames(
        date: LocalDate,
        sport: String,
        onFailure: () -> Unit,
        onSuccess: (List<ReservationDTO>) -> Unit
    ) {
        val currentEmail = Firebase.auth.currentUser!!.email!!
        withContext(Dispatchers.IO) {
            try {
                val hourFilter = if(date == LocalDate.now()){
                    LocalTime.now().hour
                } else {
                    -1
                }
                val list = db.collection("reservations")
                    .whereIn(
                        "visibilty",
                        mutableListOf(Visibilities.ON_REQUEST.name, Visibilities.PUBLIC.name)
                    )
                    .whereEqualTo("date", date.toString())

                    .get().await().documents
                    .filter { doc ->
                        val data = doc.data!!
                        val ref = data["court"] as DocumentReference
                        val sportRef = ref.id.split("_").last()
                        sportRef == sport.uppercase()
                                && (data["user"] as DocumentReference).id != Firebase.auth.currentUser!!.email!!
                                && (data["startTime"] as Long) > hourFilter
                                && !(data["accepted"] as List<DocumentReference>).contains(db.document("users/$currentEmail"))
                                && !(data["pendings"] as List<DocumentReference>).contains(db.document("users/$currentEmail"))
                    }.map {
                            async {
                                mappingReservationFromDocument(it)
                            }
                        }.map { it.await() }

                onSuccess(list)
            } catch (e: Exception) {
                println(e)
                withContext(Dispatchers.Main){
                    onFailure()
                }
            }
        }
    }

    suspend fun getUpdatedReservation(
        reservationID: String,
        onFailure: () -> Unit,
        onSuccess: (ReservationDTO) -> Unit
    ): ListenerRegistration {
        return withContext(Dispatchers.IO) {
            db.collection("reservations").document(reservationID).addSnapshotListener { value, error ->
                if(value != null && value.exists() && error == null) {
                    val reservation = runBlocking { mappingReservationFromDocument(value) }
                    onSuccess(reservation)
                } else {
                    onFailure()
                }
            }
        }
    }

    // fallo con le transazioni
    fun setVisibility(
        reservationDTO: ReservationDTO,
        visibility: Visibilities,
        onFailure: () -> Unit,
        onSuccess: (Visibilities) -> Unit
    ) {
        db.runTransaction {
            val reservationPath = db.collection("reservations").document(reservationDTO.id)
            it.update(
                reservationPath,
                mapOf(
                    "visibilty" to visibility
                )
            )
        }.addOnSuccessListener {
            onSuccess(visibility)
        }.addOnFailureListener {
            onFailure()
        }
    }

    // fallo con le transazioni
    suspend fun sendRequestToJoin(reservationDTO: ReservationDTO, onFailure: () -> Unit) {
        withContext(Dispatchers.IO){
            val resDoc = db.collection("reservations").document(reservationDTO.id)
            val res = resDoc.get().await()
            if(res["visibilty"] != Visibilities.ON_REQUEST.name){
                withContext(Dispatchers.Main){
                    onFailure()
                }
                return@withContext
            }
            val email = Firebase.auth.currentUser!!.email!!
            db.runTransaction {
                it.update(
                    resDoc,
                    mapOf(
                        "requests" to FieldValue.arrayUnion("/users/$email")
                    )
                )
            }.addOnFailureListener {
                onFailure()
            }
        }



    }

    fun acceptJoinRequest(
        reservationDTO: ReservationDTO,
        email: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->
            val currentEmailDoc = db.collection("users").document(email)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            t.update(
                    reservationDoc,
                    mapOf(
                        "requests" to FieldValue.arrayRemove(currentEmailDoc),
                        "accepted" to FieldValue.arrayUnion(currentEmailDoc)
                    )
                )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

    fun rejectJoinRequest(
        reservationDTO: ReservationDTO,
        email: String,
        onFailure: () -> Unit,
        onSuccess: () -> Unit
    ) {

        db.runTransaction { t ->
            val currentEmailDoc = db.collection("users").document(email)
            val reservationDoc = db.collection("reservations").document(reservationDTO.id)
            t
                .update(
                    reservationDoc,
                    mapOf(
                        "requests" to FieldValue.arrayRemove(currentEmailDoc)
                    )
                )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure()
        }
    }

}