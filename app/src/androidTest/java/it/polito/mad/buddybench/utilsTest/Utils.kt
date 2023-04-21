package it.polito.mad.buddybench.utilsTest

import it.polito.mad.buddybench.entities.*
import it.polito.mad.buddybench.enums.Sports
import java.time.LocalDate
import java.time.LocalTime
import java.util.Random

class Utils {

    companion object {

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

        fun createSport(): Sport {
            val sportRandom = Random().nextInt(4)
            val sportNameRandom = Sports.BASKETBALL.name
            return Sport(
                id = sportNameRandom
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

        fun createPastReservation(courtId: Int, userId: Int): Reservation {
            return Reservation(
                    userOrganizer = courtId,
                    court = userId,
                    date = LocalDate.now().minusDays(20).toString(),
                    startTime = LocalTime.of(8, 30).toString(),
                    endTime = LocalTime.of(10, 30).toString(),
            )
        }

        fun createFutureReservation(courtId: Int, userId: Int): Reservation {
            return Reservation(
                userOrganizer = courtId,
                court = userId,
                date = LocalDate.now().plusDays(20).toString(),
                startTime = LocalTime.of(8, 30).toString(),
                endTime = LocalTime.of(10, 30).toString(),
            )
        }

        fun createInvitation(reservationId: Int, userId: Int, confirmed: Boolean = false, presence: Boolean = false): Invitation {
            return Invitation (
                reservation = reservationId,
                confirmed = confirmed,
                presence = presence,
                user = userId
            )
        }

    }
}