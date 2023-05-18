package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.TimeSlotsNotAvailableException
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO

import it.polito.mad.buddybench.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationRepository {
    val db = FirebaseFirestore.getInstance()

    fun getAll(): HashMap<LocalDate, List<ReservationDTO>> {
        TODO()
        //return ReservationDTO.toHashmap(reservationDao.getAll().map { it.toReservationDTO() })
    }

    suspend fun getAllByUser(): HashMap<LocalDate, List<ReservationDTO>> {
        return withContext(Dispatchers.IO){
            val currentEmail = Firebase.auth.currentUser!!.email!!
            val result = db.collection("reservations").whereEqualTo("user", db.document("users/$currentEmail")).get()
            val resultInvitedReservations = db.collection("reservations").whereArrayContains("accepted", db.document("users/$currentEmail")).get()

            val reservations = mutableListOf<ReservationDTO>()
            for (res in result.await()){
                val reservationDTO = ReservationDTO()
                reservationDTO.date = LocalDate.parse(res.data["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
                reservationDTO.startTime = LocalTime.of((res.data["startTime"] as Long).toInt(),0)
                reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(),0)
                reservationDTO.equipment = res.data["equipment"] as Boolean
                reservationDTO.id = res.data["id"] as String
                val court = (res.data["court"] as DocumentReference).get().await()
                val acceptedUsers = (res.data["accepted"] as List<DocumentReference>).map { it.get() }.map { it.await() }.map { UserRepository.serializeUser(it.data as Map<String, Object>) }
                val pendingUsers = (res.data["pendings"] as List<DocumentReference>).map { it.get() }.map { it.await() }.map { UserRepository.serializeUser(it.data as Map<String, Object>) }
                reservationDTO.court = court.toObject(CourtDTO::class.java)!!
                reservationDTO.accepted = acceptedUsers
                reservationDTO.pendings = pendingUsers

                reservations.add(reservationDTO)
            }
            for (res in resultInvitedReservations.await()){
                val reservationDTO = ReservationDTO()
                reservationDTO.date = LocalDate.parse(res.data["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
                reservationDTO.startTime = LocalTime.of((res.data["startTime"] as Long).toInt(),0)
                reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(),0)
                reservationDTO.equipment = res.data["equipment"] as Boolean
                reservationDTO.id = res.data["id"] as String
                val court = (res.data["court"] as DocumentReference).get().await()
                val acceptedUsers = (res.data["accepted"] as List<DocumentReference>).map { it.get() }.map { it.await() }.map { UserRepository.serializeUser(it.data as Map<String, Object>) }
                reservationDTO.court = court.toObject(CourtDTO::class.java)!!
                reservationDTO.accepted = acceptedUsers
                reservationDTO.userOrganizer = UserRepository.serializeUser((res.data["user"] as DocumentReference).get().await().data!! as Map<String, Object>)
                reservations.add(reservationDTO)
            }
            ReservationDTO.toHashmap(reservations)
        }
    }

    suspend fun save(reservationDTO: ReservationDTO) {

        withContext(Dispatchers.IO) {
            val reservationMap: HashMap<String, Any> = createReservationMap(reservationDTO)
            val reservationID = reservationMap["id"] as String
            val courtName =
                reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
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


            val postResponse = db.collection("reservations")
                .document(reservationID)
                .set(reservationMap)
            if ((count + (reservationDTO.endTime.hour - reservationDTO.startTime.hour)) == slots) {
                val r =
                    db.collection("unavailable_courts").document(reservationDTO.date.toString())
                        .set(
                            mapOf("courts" to FieldValue.arrayUnion(db.document("courts/$courtName"))),
                            SetOptions.merge()
                        ).await()
            }
            postResponse.await()
        }

    }



    suspend fun update(
        reservationDTO: ReservationDTO,
        oldDate: LocalDate,
    ){
        withContext(Dispatchers.IO){
            try {
                val docCourtName = reservationDTO.court.name.replace(" ","_") + "_" + reservationDTO.court.sport
                db.collection("unavailable_courts").document(oldDate.toString()).update("courts", FieldValue.arrayRemove(db.document("courts/$docCourtName"))).await()

            }
            catch (_: Exception){
            }
            val reservationMap = createReservationMap(reservationDTO)
            reservationMap.remove("pendings")
            reservationMap.remove("accepted")
            val courtName = reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
            val court = db.collection("courts").document(courtName).get().await()
            val openingTime = (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(reservationDTO.date.dayOfWeek.name)!!.get("openingTime") as Long
            val closingTime = (court.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(reservationDTO.date.dayOfWeek.name)!!.get("closingTime") as Long
            val slots = (closingTime - openingTime).toInt()
            val allReservation = db.collection("reservations")
                .whereEqualTo("court", db.document("courts/$courtName"))
                .whereEqualTo("date", reservationDTO.date.toString())
                .get().await()
            var count = 0
            for(r in allReservation){
                val endTime = r.data["endTime"] as Long
                val startTime = r.data["startTime"] as Long
                if(reservationDTO.id == r.id) continue
                if (
                    (reservationDTO.startTime.hour in startTime until endTime)
                    ||(reservationDTO.endTime.hour in (startTime + 1)..endTime)){
                    throw TimeSlotsNotAvailableException()
                }
                count = (count + (endTime - startTime)).toInt()
            }
            val postResponse = db.collection("reservations")
                .document(reservationDTO.id)
                .update(reservationMap)

            if ((count + (reservationDTO.endTime.hour - reservationDTO.startTime.hour)) == slots ) {
                val r = db.collection("unavailable_courts").document(reservationDTO.date.toString())
                    .set(
                        mapOf("courts" to FieldValue.arrayUnion(db.document("courts/$courtName"))),
                        SetOptions.merge()
                    ).await()
            }
            postResponse.await()
        }
    }




    suspend fun delete(
        reservationDTO: ReservationDTO,
        oldDate: LocalDate,
    ) {

        withContext(Dispatchers.IO){
            val docName = reservationDTO.id
            val docCourtName = reservationDTO.court.name.replace(" ","_") + "_" + reservationDTO.court.sport
            val responseOne = db.collection("reservations").document(docName).delete()
            val responseTwo = db.collection("unavailable_courts").document(oldDate.toString()).update("courts", FieldValue.arrayRemove(db.document("courts/$docCourtName")))
            responseOne.await()
            try{
                responseTwo.await()
            } catch (_: Exception){
            }
        }
    }



    suspend fun getReservation(
        reservationID: String
    ): ReservationDTO {
        return withContext(Dispatchers.IO){
            val res = db.collection("reservations")
                .document(reservationID)
                .get().await()
            val reservationDTO = ReservationDTO()
            reservationDTO.date = LocalDate.parse(res.data!!["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
            reservationDTO.startTime = LocalTime.of((res.data!!["startTime"] as Long).toInt(),0)
            reservationDTO.endTime = LocalTime.of((res.data!!["endTime"] as Long).toInt(),0)
            reservationDTO.equipment = res.data!!["equipment"] as Boolean
            reservationDTO.id = res.data!!["id"] as String
            reservationDTO

        }
    }

    private fun createReservationMap(reservationDTO: ReservationDTO): HashMap<String, Any>{
        val courtName = reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
        val reservationID = if (reservationDTO.id != "") {reservationDTO.id} else {Utils.generateUUID()}
        val reservationMap: HashMap<String, Any> = HashMap()
        reservationMap["court"] = db.document("courts/$courtName")
        reservationMap["user"] = db.document("users/${reservationDTO.userOrganizer.email}")
        reservationMap["date"] = reservationDTO.date.toString()
        reservationMap["startTime"] = reservationDTO.startTime.hour
        reservationMap["endTime"] = reservationDTO.endTime.hour
        reservationMap["equipment"] = reservationDTO.equipment
        reservationMap["accepted"] = listOf<DocumentReference>()
        reservationMap["pendings"] = listOf<DocumentReference>()
        reservationMap["id"] = reservationID
        return  reservationMap
    }
}