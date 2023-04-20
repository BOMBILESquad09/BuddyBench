package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.entities.Reservation
import java.time.LocalDate
import java.time.LocalTime

class ReservationDTO(
    val userOrganizer: Int,
    val court: Int, val date: LocalDate, val startTime: LocalTime,
    val endTime: LocalTime
) {


    fun toEntity(): Reservation {
        return Reservation(
            userOrganizer = this.userOrganizer,
            court = this.court,
            startTime = this.startTime.toString(),
            endTime = this.endTime.toString(),
            date = this.date.toString(),
        )
    }


    companion object{
        fun mockReservationDTOs(): HashMap<LocalDate,List<ReservationDTO>>{
            val now = LocalDate.now()
            val later = now.plusDays(10)
            val timeNow = LocalTime.now()
            val endNow = timeNow.plusHours(1)
            val timeLaterEnd = timeNow.plusHours(3)
            val list =  listOf(ReservationDTO(0,0, now,timeNow, endNow, ),
                    ReservationDTO(0,0, later, timeNow, timeLaterEnd),
                ReservationDTO(0,0, later, timeNow, timeLaterEnd),
                ReservationDTO(0,0, later, timeNow, timeLaterEnd),
                ReservationDTO(0,0, later, timeNow, timeLaterEnd),
                ReservationDTO(0,0, later, timeNow, timeLaterEnd),
                ReservationDTO(0,0, later, timeNow, timeLaterEnd),

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

