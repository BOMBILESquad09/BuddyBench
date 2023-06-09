package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.CourtTimeTableDTO
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.entities.toCourtDTO
import it.polito.mad.buddybench.persistence.entities.toCourtTimeDTO
import it.polito.mad.buddybench.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.security.auth.callback.Callback
import kotlin.math.max

class CourtRepository {
    val db = FirebaseFirestore.getInstance()

    //used only for inserting fast the existing data on firebase
    fun addTimetable(timeTableDTO: CourtTimeTableDTO) {
        val name = timeTableDTO.court.name.replace(" ", "_") + "_" + timeTableDTO.court.sport
        val timetable: HashMap<String, HashMap<String, Int>> = HashMap()
        for (t in timeTableDTO.timeTable) {
            val hash: HashMap<String, Int> = HashMap()
            hash["openingTime"] = t.value.first.hour
            hash["closingTime"] = t.value.second.hour
            timetable[t.key.name] = hash
        }

        db.collection("courts")
            .document(name)
            .update("timetable", timetable)
            .addOnSuccessListener {
            }
    }


    suspend fun getCourt(
        name: String,
        sport: String,
        onFailure: () -> Unit,
        onSuccess: (CourtDTO) -> Unit
    ) {
        try {
            withContext(Dispatchers.IO) {
                val courtName = name.replace(" ", "_") + "_" + sport
                val res = db.collection("courts")
                    .document(courtName)
                    .get().await()
                val court = res.toObject(CourtDTO::class.java)!!
                onSuccess(court)
            }
        } catch (e: Exception) {
            onFailure()
        }

    }

    fun getCourtTimeTable(
        name: String,
        sport: Sports,
        onFailure: () -> Unit,
        onSuccess: (CourtTimeTableDTO) -> Unit
    ) {
        val courtName = name.replace(" ", "_") + "_" + sport.name
        db.collection("courts").document(courtName).get()
            .addOnSuccessListener {
                try {
                    val courtDTO = it.toObject(CourtDTO::class.java)!!
                    val timetable: HashMap<DayOfWeek, Pair<LocalTime, LocalTime>> = HashMap()
                    for (s in it.data!!["timetable"] as HashMap<String, HashMap<String, Long>>) {
                        val openingTime = s.value["openingTime"]!!
                        val closingTime = s.value["closingTime"]!!
                        timetable.put(
                            DayOfWeek.valueOf(s.key),
                            Pair(
                                LocalTime.of(openingTime.toInt(), 0),
                                LocalTime.of(closingTime.toInt(), 0)
                            )
                        )
                    }
                    if (courtDTO.facilities == null)
                        courtDTO.facilities = listOf()
                    val courtTimeTableDTO = CourtTimeTableDTO(courtDTO, timetable)
                    onSuccess(courtTimeTableDTO)
                } catch (e: Exception) {
                    onFailure()
                }
            }

    }


    fun getCourtsByDay(
        sport: Sports,
        dayOfWeek: LocalDate,
        onFailure: () -> Unit,
        onSuccess: (List<CourtDTO>) -> Unit
    ) {
        fun filterByMaxReservation(courts: List<CourtDTO>, onSuccess: (List<CourtDTO>) -> Unit) {
            var finalCourts = courts
            if (dayOfWeek == LocalDate.now() && courts.isNotEmpty()) {
                try {
                    db
                        .collection("reservations")
                        .whereIn("court", courts.map { db.document("courts/${it.getId()}") })
                        .whereEqualTo("date", LocalDate.now().toString()).get()
                        .addOnSuccessListener {
                            val timeSlotsOccupied = HashMap<String, MutableList<LocalTime>>()
                            for (r in it) {
                                val endTime = LocalTime.of((r.data["endTime"] as Long).toInt(), 0)
                                val startTime =
                                    LocalTime.of((r.data["startTime"] as Long).toInt(), 0)


                                val courtId = (r.data["court"] as DocumentReference).id

                                if (timeSlotsOccupied[courtId] != null) {
                                    timeSlotsOccupied[courtId]!!.addAll(
                                        Utils.getTimeSlots(
                                            startTime,
                                            endTime
                                        )
                                    )
                                } else {
                                    timeSlotsOccupied[courtId] =
                                        Utils.getTimeSlots(startTime, endTime).toMutableList()
                                    timeSlotsOccupied[courtId]!!.add(LocalTime.now().truncatedTo(ChronoUnit.HOURS))
                                }
                            }



                            finalCourts = finalCourts.filter { c ->
                                if (timeSlotsOccupied[c.getId()] != null) {

                                    !timeSlotsOccupied[c.getId()]!!.contains(
                                        LocalTime.now().truncatedTo(ChronoUnit.HOURS)
                                    )
                                } else {
                                    true
                                }
                            }


                            onSuccess(finalCourts)
                            return@addOnSuccessListener
                        }
                } catch (e: Exception) {
                    onFailure()
                    return
                }
            } else {
                onSuccess(courts)

            }
        }


        db.collection("courts")
            .whereEqualTo("sport", sport.name)
            .whereGreaterThan("timetable.${dayOfWeek.dayOfWeek.name}.openingTime", -1)
            .get().addOnSuccessListener { courtsDoc ->
                db.collection("unavailable_courts")
                    .document(dayOfWeek.toString())
                    .get()
                    .addOnSuccessListener { it ->
                        try {
                            var courts = courtsDoc.toObjects(CourtDTO::class.java)
                            if (dayOfWeek == LocalDate.now()) {
                                courts = courts.filter {
                                    (it.timetable[dayOfWeek.dayOfWeek.name]!!.closingTime - 1) > LocalTime.now().hour
                                }
                            }
                            if (it.data != null) {
                                val courtsToDelete =
                                    (it.data!!["courts"] as List<DocumentReference>).map {
                                        val tokens = it.id.split("_").toMutableList()
                                        tokens.removeLast()
                                        tokens.joinToString(" ")
                                    }
                                filterByMaxReservation(
                                    courts.filter { c -> !courtsToDelete.contains(c.name) },
                                    onSuccess
                                )
                            } else {
                                filterByMaxReservation(courts, onSuccess)
                            }
                        } catch (e: Exception) {
                            println (e)
                            onFailure()
                        }
                    }.addOnFailureListener {
                        onFailure()
                    }
            }
    }

    fun getTimeSlotsOccupiedForCourtAndDate(
        court: CourtDTO,
        date: LocalDate,
        onFailure: () -> Unit,
        onSuccess: (List<LocalTime>) -> Unit
    ) {
        val courtName = court.getId()

        db
            .collection("reservations")
            .whereEqualTo("court", db.document("courts/$courtName"))
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener {
                try {
                    val timeslots = mutableListOf<ReservationDTO>()
                    for (res in it) {
                        val reservationDTO = ReservationDTO()
                        reservationDTO.startTime =
                            LocalTime.of((res.data["startTime"] as Long).toInt(), 0)
                        reservationDTO.endTime =
                            LocalTime.of((res.data["endTime"] as Long).toInt(), 0)
                        timeslots.add(reservationDTO)

                    }
                    onSuccess(
                        timeslots.map { Utils.getTimeSlots(it.startTime, it.endTime) }.flatten()
                            .toList()
                    )

                } catch (e: Exception) {
                    onFailure()
                }
            }

    }

    fun checkIfPlayed(
        courtName: String,
        sport: String,
        userEmail: String,
        onFailure: () -> Unit,
        onSuccess: (Boolean) -> Unit
    ) {
        val courtDocName = courtName.replace(" ", "_") + "_" + sport
        db.collection("reservations")
            .whereEqualTo("user", db.document("users/$userEmail"))
            .whereEqualTo("court", db.document("courts/$courtDocName"))
            .get()
            .addOnSuccessListener {
                try {
                    if (it.size() == 0) {
                        onSuccess(false)
                        return@addOnSuccessListener
                    }
                    val dates = it.map { d ->
                        Pair(
                            LocalDate.parse(
                                d.data["date"] as String,
                                DateTimeFormatter.ISO_LOCAL_DATE
                            ), (d.data["endTime"] as Long).toInt()
                        )
                    }
                    val minDate = dates.minOfOrNull { d -> d.first }!!
                    val minEndTime =
                        dates.filter { t -> t.first == minDate }.minOfOrNull { t -> t.second }!!

                    if (minDate == LocalDate.now()) {
                        onSuccess(minEndTime <= LocalTime.now().hour)
                    } else {
                        onSuccess(minDate < LocalDate.now())
                    }
                } catch (e: Exception) {
                    onFailure()
                }
            }

    }
}