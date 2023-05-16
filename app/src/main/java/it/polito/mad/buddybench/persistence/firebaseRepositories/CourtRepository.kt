package it.polito.mad.buddybench.persistence.firebaseRepositories

import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import it.polito.mad.buddybench.persistence.dto.CourtTimeTableDTO
import it.polito.mad.buddybench.persistence.entities.toCourtDTO
import it.polito.mad.buddybench.persistence.entities.toCourtTimeDTO
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
            timetable.put(t.key.name, hash)

        }

        db.collection("courts")
            .document(name)
            .update("timetable", timetable)
            .addOnSuccessListener {
                println("done")
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

                val courts = it.toObjects(CourtDTO::class.java)
                println(courts.size)
                callback(courts)
            }
    }
}