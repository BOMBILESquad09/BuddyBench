package it.polito.mad.buddybench.DTO

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.*
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.utils.Utils
import java.time.LocalDate
import java.time.LocalTime

class ReservationDTO(val userOrganizer: UserDTO,
                     val court: CourtDTO,
                     val date: LocalDate,
                     val startTime: LocalTime,
                     val endTime: LocalTime
) {


//    fun toEntity(): Reservation {
//        return Reservation(
//            userOrganizer = this.userOrganizer,
//            court = this.court,
//            startTime = this.startTime.toString(),
//            endTime = this.endTime.toString(),
//            date = this.date.toString(),
//        )
//    }


    companion object{

        fun createUser(): User {
            return User(
                name = "Vittorio",
                surname = "Arpino",
                nickname = "Victor",
                birthdate = LocalDate.now().toString(),
                location = "Scafati",
                email = "vittorio@polito.it",
                reliability = 10
            )
        }

        fun createCourt(sport: String, feeHour: Int = 20): Court {
            return Court(
                courtName = "CourtSampleName",
                address = "ExampleRoad",
                feeHour = feeHour,
                sport = sport
            )
        }

        fun mockReservationDTOs(): HashMap<LocalDate,List<ReservationDTO>>{
            val now = LocalDate.now()
            val later = now.plusDays(10)
            val timeNow = LocalTime.now()
            val endNow = timeNow.plusHours(1)
            val timeLaterEnd = timeNow.plusHours(3)
            val list =  listOf(ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), now,timeNow, endNow, ),
                    ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),
                ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),
                ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),
                ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),
                ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),
                ReservationDTO(createUser().toUserDTO(),createCourt(Sports.toJSON(Sports.TENNIS)).toCourtDTO("Tennis"), later, timeNow, timeLaterEnd),

            )
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
}

fun ReservationDTO.toEntity(userOrganizer: Int, court: Int): Reservation {
    return Reservation (
        userOrganizer = userOrganizer,
        court = court,
        startTime = this.startTime.toString(),
        date = this.date.toString(),
    )
}



