package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.*
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalDate
import java.time.LocalTime

class ReservationDTO(
) {
    constructor(userOrganizer: UserDTO, court: CourtDTO, date: LocalDate, startTime: LocalTime, endTime: LocalTime, equipment: Boolean): this(){
        this.userOrganizer = userOrganizer
        this.court = court
        this.date = date
        this.startTime = startTime
        this.endTime = endTime
        this.equipment = equipment
    }
    lateinit var  userOrganizer: UserDTO
    lateinit var court: CourtDTO
    lateinit var date: LocalDate
    lateinit var startTime: LocalTime
    lateinit var endTime: LocalTime
    var equipment: Boolean = false
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
        fun mockReservationDTOs(): HashMap<LocalDate,List<ReservationDTO>>{
            val now = LocalDate.now()
            val later = now.plusDays(10)
            val timeNow = LocalTime.now()
            val endNow = timeNow.plusHours(1)
            val timeLaterEnd = timeNow.plusHours(3)
            val list =  listOf(
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO(), now,timeNow, endNow, false),
                    ReservationDTO(
                        createUser().toUserDTO(),
                        createCourt(Sports.toJSON(Sports.BASKETBALL)).toCourtDTO(), later, timeNow, timeLaterEnd, false),
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.FOOTBALL)).toCourtDTO(), later, timeNow, timeLaterEnd, false),
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.VOLLEYBALL)).toCourtDTO(), later, timeNow, timeLaterEnd, false),
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO(), later, timeNow, timeLaterEnd, false),
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO(), later, timeNow, timeLaterEnd, false),
                ReservationDTO(
                    createUser().toUserDTO(),
                    createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO(), later, timeNow, timeLaterEnd, false)
            )
            return toHashmap(list)
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
}



