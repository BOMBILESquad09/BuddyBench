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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.security.auth.callback.Callback

class CourtRepository {
    val db = FirebaseFirestore.getInstance()

    fun addTimetable(timeTableDTO: CourtTimeTableDTO){
        val name = timeTableDTO.court.name.replace(" ", "_") + "_" + timeTableDTO.court.sport
        val timetable: HashMap<String, HashMap<String, Int>> = HashMap()
        for (t in timeTableDTO.timeTable){
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

    fun getCourt(name:String, sport: String, callback: (CourtDTO) -> Unit){
        val courtName = name.replace(" ", "_") + "_" + sport
        db.collection("courts")
            .document(courtName)
            .get()
            .addOnSuccessListener {
                val court = it.toObject(CourtDTO::class.java)!!
                callback(court)
            }
    }

    fun getCourtTimeTable(name: String, sport: Sports, callback: (CourtTimeTableDTO) -> Unit) {
        val courtName = name.replace(" ", "_") + "_" + sport.name
        val court = db.collection("courts").document(courtName).get()
            .addOnSuccessListener{
                val courtDTO = it.toObject(CourtDTO::class.java)!!
                val timetable: HashMap<DayOfWeek, Pair<LocalTime, LocalTime>> = HashMap()
                for (s in it.data!!["timetable"] as HashMap<String, HashMap<String, Long>>){
                    val openingTime = s.value["openingTime"]!!
                    val closingTime = s.value["closingTime"]!!
                    timetable.put(DayOfWeek.valueOf(s.key), Pair(LocalTime.of(openingTime.toInt(),0), LocalTime.of(closingTime.toInt(), 0)))
                }
                if(courtDTO.facilities == null)
                    courtDTO.facilities = listOf()
                val courtTimeTableDTO = CourtTimeTableDTO(courtDTO, timetable )
                callback(courtTimeTableDTO)
        }


    }


    fun getCourtsByDay(sport: Sports, dayOfWeek: LocalDate, callback: (List<CourtDTO>) -> Unit ) {
        db.collection("courts")
            .whereEqualTo("sport", sport.name)
            .whereGreaterThan("timetable.${dayOfWeek.dayOfWeek.name}.openingTime", 0)
            .get().addOnSuccessListener {
                courtsDoc ->
                db.collection("unavailable_courts")
                    .document(dayOfWeek.toString())
                    .get()
                    .addOnSuccessListener { it ->
                        val courts = courtsDoc.toObjects(CourtDTO::class.java)
                        if (it.data != null){
                            val courtsToDelete = (it.data!!["courts"] as List<DocumentReference>).map {
                                val tokens = it.id.split("_").toMutableList()
                                tokens.removeLast()
                                tokens.joinToString(" ")
                            }
                            callback(courts.filter { c -> !courtsToDelete.contains(c.name) })
                        } else{
                            callback(courts)
                        }

                    }
            }
    }

    fun getTimeSlotsOccupiedForCourtAndDate(court: CourtDTO, date: LocalDate, callback: (List<LocalTime>) -> Unit) {
        val courtName = court.name.replace(" ", "_") + "_" + court.sport
        db
            .collection("reservations")
            .whereEqualTo("court", db.document("courts/$courtName"))
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener {
                val timeslots = mutableListOf<ReservationDTO>()
                for( res in it){
                    val reservationDTO = ReservationDTO()
                    reservationDTO.startTime = LocalTime.of((res.data["startTime"] as Long).toInt(), 0 )
                    reservationDTO.endTime = LocalTime.of((res.data["endTime"] as Long).toInt(), 0)
                    timeslots.add(reservationDTO)

                }
                callback(timeslots.map { Utils.getTimeSlots(it.startTime, it.endTime) }.flatten().toList())

            }

    }

    fun checkIfPlayed(courtName: String, sport: String, userEmail: String, callback: (Boolean) -> Unit) {
        val courtDocName = courtName.replace(" ", "_") + "_" + sport
        db.collection("reservations")
            .whereEqualTo("user", db.document("users/$userEmail"))
            .whereEqualTo("court", db.document("courts/$courtDocName"))

            .get()
            .addOnSuccessListener {
                if (it.size() == 0) {
                    callback(false)
                    return@addOnSuccessListener
                }
                val dates = it.map { Pair(LocalDate.parse(it.data["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE), (it.data["endTime"] as Long).toInt()) }
                val minDate = dates.minOfOrNull { d -> d.first }!!
                val minEndTime = dates.filter { t -> t.first == minDate }.minOfOrNull { t -> t.second }!!

                if(minDate == LocalDate.now()){
                    callback(minEndTime <= LocalTime.now().hour )
                } else {
                    callback(minDate < LocalDate.now())
                }
            }
    }
}