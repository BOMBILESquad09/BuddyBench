package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.entities.*
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalDate
import java.time.LocalTime

class ReservationDTO(
) {
    constructor(id: String, userOrganizer: Profile, court: CourtDTO, date: LocalDate, startTime: LocalTime, endTime: LocalTime, equipment: Boolean): this(){
        this.id = id
        this.userOrganizer = userOrganizer
        this.court = court
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.equipment = equipment
        this.accepted = listOf()
        this.pendings = listOf()
    }

    lateinit var id: String
    lateinit var  userOrganizer: Profile
    lateinit var court: CourtDTO
    lateinit var date: LocalDate
    lateinit var startTime: LocalTime
    lateinit var endTime: LocalTime
    var equipment: Boolean = false
    lateinit var accepted: List<Profile>
    lateinit var pendings: List<Profile>
    companion object{

        private fun createUser(): User {
            return User(
                name = "Vittorio",
                surname = "Arpino",
                nickname = "Victor",
                birthdate = LocalDate.now().toString(),
                location = "Scafati",
                email = "vittorio@polito.it",
                reliability = 10,
                imagePath = null
            )
        }

        private fun createCourt(sport: String, feeHour: Int = 20, feeEquipment: Int = 10): Court {
            return Court(
                name = "CourtSampleName",
                address = "ExampleRoad",
                feeHour = feeHour,
                sport = sport,
                phoneNumber = "ExampleNumber",
                location= "ExampleLocation",
                path = "court1",
                feeEquipment = feeEquipment,
                rating = 4.2,
                nReviews = 15
            )
        }

        fun toHashmap(list: List<ReservationDTO>): HashMap<LocalDate,List<ReservationDTO>>{

            val hm = HashMap<LocalDate, MutableList<ReservationDTO>>()
            val entries  = list.forEach{
                val l = hm[it.date]
                if(l == null){
                    hm[it.date] = mutableListOf(it)
                } else {
                    l.add(it)
                }
            }
            return hm as HashMap<LocalDate, List<ReservationDTO>>
        }

    }

    fun toEntity(userOrganizer: Int, court: Int, equipment: Boolean): Reservation {
        return Reservation (
            userOrganizer = userOrganizer,
            court = court,
            startTime = this.startTime.hour,
            endTime = this.endTime.hour,
            date = this.date.toString(),
            equipment = equipment

        )
    }

    fun isUserOrganizerInitialized() = ::userOrganizer.isInitialized
}



