package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.persistence.entities.CourtWithSport
import it.polito.mad.buddybench.persistence.entities.Reservation
import it.polito.mad.buddybench.persistence.entities.UnavailableDayCourt
import it.polito.mad.buddybench.persistence.entities.toReservationDTO
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationRepository {
    val db = FirebaseFirestore.getInstance()

    fun getAll(): HashMap<LocalDate, List<ReservationDTO>> {
        TODO()
        //return ReservationDTO.toHashmap(reservationDao.getAll().map { it.toReservationDTO() })
    }

    fun getAllByUser(email: String, callback:  (HashMap<LocalDate, List<ReservationDTO>>) -> Unit) {
        db.collection("reservations")
            .whereEqualTo("user", db.document("users/$email"))
            .get()
            .addOnSuccessListener {
                docs ->

                val reservations = mutableListOf<ReservationDTO>()
                var count = 0
                for(res in docs){
                    count = count + 1
                    val reservationDTO = ReservationDTO()
                    reservationDTO.date = LocalDate.parse(res.data["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
                    reservationDTO.startTime = LocalTime.of((res.data["startTime"] as Long).toInt(),0)
                    reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(),0)
                    reservationDTO.equipment = res.data["equipment"] as Boolean
                    reservationDTO.court = CourtDTO()
                    val court = (res.data["court"] as DocumentReference)
                    court.get().addOnSuccessListener {
                        reservationDTO.court = it.toObject(CourtDTO::class.java)!!
                        reservations.add(reservationDTO)
                        if(count == docs.size()) {
                            callback(ReservationDTO.toHashmap(
                                reservations))
                        }

                    }

                }

            }
        //return ReservationDTO.toHashmap( reservationDao.getAllByUser(email).map { it.toReservationDTO() })
    }

    fun save(reservationDTO: ReservationDTO, callback: () -> Unit) {
        val courtName = reservationDTO.court.name.replace(" ", "_") + "_" + reservationDTO.court.sport
        val reservationMap: HashMap<String, Any> = HashMap()
        reservationMap["court"] = db.document("courts/$courtName")
        reservationMap["user"] = db.document("users/${reservationDTO.userOrganizer.email}")
        reservationMap["date"] = reservationDTO.date.toString()
        reservationMap["startTime"] = reservationDTO.startTime.hour
        reservationMap["endTime"] = reservationDTO.endTime.hour
        reservationMap["equipment"] = reservationDTO.equipment
        db.collection("courts")
            .document(courtName)
            .get()
            .addOnSuccessListener {
                val openingTime = (it.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(reservationDTO.date.dayOfWeek.name)!!.get("openingTime") as Long
                val closingTime = (it.data!!["timetable"] as HashMap<String, HashMap<String, Long>>).get(reservationDTO.date.dayOfWeek.name)!!.get("closingTime") as Long
                val slots = (closingTime - openingTime).toInt()
                db.collection("reservations")
                    .whereEqualTo("court", db.document("courts/$courtName"))
                    .whereEqualTo("date", reservationDTO.date.toString())
                    .get()
                    .addOnSuccessListener{
                        var count = 0
                        for(r in it){
                            val endTime = r.data["endTime"] as Long
                            val startTime = r.data["startTime"] as Long
                            if ((reservationDTO.startTime.hour >= startTime && reservationDTO.startTime.hour < endTime)
                                ||(reservationDTO.endTime.hour <=endTime && reservationDTO.endTime.hour > startTime)){
                                //ERROREEEEEEEEEEEE
                                return@addOnSuccessListener
                            }
                            count = (count + (endTime - startTime)).toInt()
                        }

                        if ((count + (reservationDTO.endTime.hour - reservationDTO.startTime.hour)) == slots ){
                            db
                                .collection("unavailable_courts")
                                .document(reservationDTO.date.toString())
                                .set(mapOf("courts" to FieldValue.arrayUnion(db.document("courts/$courtName"))), SetOptions.merge())
                                .addOnSuccessListener {
                                    db.collection("reservations")
                                    .document(courtName + "_" + reservationDTO.userOrganizer.email + "_" + reservationDTO.date.toString() + "_" + reservationDTO.startTime.hour)
                                    .set(reservationMap)
                                    .addOnSuccessListener {
                                        callback()
                                    }
                                }
                        } else{
                            db
                                .collection("reservations")
                                .document(courtName + "_" + reservationDTO.userOrganizer.email + "_" + reservationDTO.date.toString() + "_" + reservationDTO.startTime.hour)
                                .set(reservationMap)
                                .addOnSuccessListener {
                                    callback()
                                }
                        }
                    }
            }
    }

    fun update(
        reservationDTO: ReservationDTO,
        oldDate: LocalDate,
        oldStartTime: Int,
        callback: () -> Unit
    ){



        delete(reservationDTO.court.name,
            Sports.valueOf(reservationDTO.court.sport),
            LocalTime.of(oldStartTime, 0),
            reservationDTO.userOrganizer.email, oldDate ){
            save(reservationDTO){callback()}
        }


    }


    private fun updateUnavailableDayCourt(
        reservationDTO: ReservationDTO,
        courtWithSport: CourtWithSport
    ) {
        TODO()
        /*
        val reservations = reservationDao.getAllByCourtAndDate(
            courtWithSport.court.id, reservationDTO.date.format(
                DateTimeFormatter.ISO_LOCAL_DATE
            )
        )
        reservations.map { it.reservation.endTime - it.reservation.startTime }.reduce { a, b ->
            a + b
        }.let {
            val time = courtTimeDao.getDayTimeByCourt(
                courtWithSport.court.id,
                reservationDTO.date.dayOfWeek.value
            )!!
            if ((time.courtTime.closingTime - time.courtTime.openingTime) <= it) {
                unavailableDayCourtDao.save(
                    UnavailableDayCourt(
                        courtWithSport.court.id, reservationDTO.date.format(
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )
                    )
                )
            } else {
                unavailableDayCourtDao.delete(
                    UnavailableDayCourt(
                        courtWithSport.court.id, reservationDTO.date.format(
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )
                    )
                )
            }
        }*/
    }

    fun delete(
        courtName: String,
        sport: Sports,
        startTime: LocalTime,
        email: String,
        date: LocalDate,
        callback: () -> Unit
    ) {
        val docName = courtName.replace(" ", "_") + "_" + sport.name + "_" + email + "_" + date.toString() + "_" + startTime.hour
        val docCourtName = courtName.replace(" ", "_") + "_" + sport.name
        db
            .collection("reservations")
            .document(docName)
            .delete()
            .addOnSuccessListener {
                db.collection("unavailable_courts")
                    .document(date.toString())
                    .update("courts", FieldValue.arrayRemove(db.document("courts/$docCourtName")))
                    .addOnSuccessListener {
                        callback() }
                 }
        /*
        val user = userDao.getUserByEmail(email)!!

        val court = courtDao.getByNameAndSportPlain(courtName, sport.toString().uppercase())

        val reservation = reservationDao.getReservationPlain(
            user.user.id, court.id,
            date.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.hour
        )

        reservationDao.delete(reservation)
        unavailableDayCourtDao.delete(
            UnavailableDayCourt(
                court.id, reservation.date.format(
                    DateTimeFormatter.ISO_LOCAL_DATE
                )
            )
        )*/
    }



    fun getReservation(
        courtName: String,
        sportInCourt: String,
        email: String,
        date: LocalDate,
        startTime: Int
    ): ReservationDTO {
        TODO()
        /*val court = courtDao.getByNameAndSport(courtName, sportInCourt.uppercase())
        val user = userDao.getUserByEmail(email)

        val reservation = reservationDao.getReservation(
            user!!.user.email,
            court.court.id,
            date.toString(),
            startTime

        )
        return reservation.toReservationDTO()*/

    }
}