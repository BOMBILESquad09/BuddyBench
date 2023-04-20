package it.polito.mad.buddybench

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polito.mad.buddybench.dao.*
import it.polito.mad.buddybench.database.CourtReservationDatabase
import it.polito.mad.buddybench.entities.*
import it.polito.mad.buddybench.utilsTest.Utils
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: CourtReservationDatabase
    private lateinit var userDao: UserDao
    private lateinit var courtDao: CourtDao
    private lateinit var userSportDao: UserSportDao
    private lateinit var sportDao: SportDao
    private lateinit var courtTimeDao: CourtTimeDao
    private lateinit var reservationDao: ReservationDao
    private lateinit var invitationDao: InvitationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CourtReservationDatabase::class.java).build()
        userDao = db.userDao()
        courtDao = db.courtDao()
        userSportDao = db.userSportDao()
        sportDao = db.sportDao()
        courtTimeDao = db.courtTimeDao()
        reservationDao = db.reservationDao()
        invitationDao = db.invitationDao()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val user = Utils.createUser()
        userDao.save(user)
        val userInserted = userDao.getAll()

        assert(userInserted.isNotEmpty())
        assertThat(userInserted.first().name, equalTo(user.name))
        assertThat(userInserted.first().surname, equalTo(user.surname))
        assertThat(userInserted.first().email, equalTo(user.email))
        assertThat(userInserted.first().birthdate, equalTo(user.birthdate))
        assertThat(userInserted.first().location, equalTo(user.location))
        assertThat(userInserted.first().reliability, equalTo(user.reliability))

    }

    @Test
    @Throws(Exception::class)
    fun writeSportAndReadInList() {
        val sport = Utils.createSport()
        sportDao.save(sport)
        val sportsInserted = sportDao.getAll()

        assert(sportsInserted.isNotEmpty())
        assertThat(sportsInserted.first().sportName, equalTo(sport.sportName))
    }

    @Test
    @Throws(Exception::class)
    fun writeCourtAndReadInList() {
        val sport = Utils.createSport()
        sportDao.save(sport)

        val sportInserted = sportDao.getAll()[0]
        val court = Utils.createCourt(sportInserted.id)
        courtDao.save(court)

        val courtInserted = courtDao.getAll()
        assert(courtInserted.isNotEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun writeCourtTimeAndReadInList() {
        val sport = Utils.createSport()
        sportDao.save(sport)
        val sportInserted = sportDao.getAll()[0]
        val court = Utils.createCourt(sportInserted.id)
        courtDao.save(court)
        val courtInserted = courtDao.getAll()[0]

        val courtTime = CourtTime(
            court = courtInserted.id,
            openingTime = LocalTime.of(8, 30).toString(),
            closingTime = LocalTime.of(20, 30).toString(),
            dayOfWeek = LocalDate.now().dayOfWeek.toString()
        )
        courtTimeDao.save(courtTime)
        val courtTimeInserted = courtTimeDao.getAll()[0]
        assertThat(courtTimeInserted.dayOfWeek, equalTo(courtTime.dayOfWeek))
    }

    @Test
    @Throws(Exception::class)
    fun writeInvitationAndReadInList() {
        val sport = Sport(
            sportName = "Tennis"
        )
        sportDao.save(sport)
        val sportInserted = sportDao.getAll()[0]
        val court = Court(
            courtName = "CourtSampleName",
            address = "ExampleRoad",
            feeHour = 20,
            sport = sportInserted.id
        )
        courtDao.save(court)
        val courtInserted = courtDao.getAll()[0]
        val user = User(
            name = "Vittorio",
            surname = "Arpino",
            nickname = "Victor",
            birthdate = LocalDate.now().toString(),
            location = "Scafati",
            email = "vittorio@polito.it",
            reliability = 10
        )
        userDao.save(user)
        val userInserted = userDao.getAll()[0]
        val reservation = Reservation(
            userOrganizer = userInserted.id,
            court = courtInserted.id,
            date = LocalDate.now().toString(),
            startTime = LocalTime.of(8, 30).toString(),
            endTime = LocalTime.of(10, 30).toString(),
        )
        reservationDao.save(reservation)
        val reservationInserted = reservationDao.getAll()[0]
        val invitation = Invitation(
            reservation = reservationInserted.id,
            confirmed = true,
            presence = true,
            user = userInserted.id
        )
        invitationDao.save(invitation)
        val invitationInserted = invitationDao.getAll()[0]
        assertThat(invitationInserted.confirmed, equalTo(invitation.confirmed))
    }

    @Test
    @Throws(Exception::class)
    fun writeReservationAndReadInList() {
        val sport = Sport(
            sportName = "Tennis"
        )
        sportDao.save(sport)
        val sportInserted = sportDao.getAll()[0]
        val court = Court(
            courtName = "CourtSampleName",
            address = "ExampleRoad",
            feeHour = 20,
            sport = sportInserted.id
        )
        courtDao.save(court)
        val courtInserted = courtDao.getAll()[0]
        val user = User(
            name = "Vittorio",
            surname = "Arpino",
            nickname = "Victor",
            birthdate = LocalDate.now().toString(),
            location = "Scafati",
            email = "vittorio@polito.it",
            reliability = 10
        )
        userDao.save(user)
        val userInserted = userDao.getAll()[0]
        val reservation = Reservation(
            userOrganizer = userInserted.id,
            court = courtInserted.id,
            date = LocalDate.now().toString(),
            startTime = LocalTime.of(8, 30).toString(),
            endTime = LocalTime.of(10, 30).toString(),
        )
        reservationDao.save(reservation)
        val reservationInserted = reservationDao.getAll()[0]
        assertThat(reservationInserted.startTime, equalTo(reservationInserted.startTime))
    }

    @Test
    @Throws(Exception::class)
    fun getFutureReservations() {
        val sport = Utils.createSport()
        sportDao.save(sport)

        val user = Utils.createUser()
        userDao.save(user)

        val sportInserted = sportDao.getAll()[0]
        val court = Utils.createCourt(sportInserted.id)
        courtDao.save(court)

        val courtInserted = courtDao.getAll()[0]
        val userInserted = userDao.getAll()[0]
        val reservationFuture = Utils.createFutureReservation(courtInserted.id, userInserted.id)
        val reservationPast = Utils.createPastReservation(courtInserted.id, userInserted.id)
        reservationDao.save(reservationFuture)
        reservationDao.save(reservationPast)

        val futureReservationList = reservationDao.getFutureReservationByEmail(user.email, LocalDate.now().toString())
        assert(futureReservationList.isNotEmpty())
        assert(futureReservationList.size == 1)

    }

    @Test
    @Throws(Exception::class)
    fun getFutureInvitations() {
        val sport = Utils.createSport()
        sportDao.save(sport)

        val user = Utils.createUser()
        userDao.save(user)

        val sportInserted = sportDao.getAll()[0]
        val court = Utils.createCourt(sportInserted.id)
        courtDao.save(court)

        val courtInserted = courtDao.getAll()[0]
        val userInserted = userDao.getAll()[0]
        val reservationFuture = Utils.createFutureReservation(courtInserted.id, userInserted.id)
        val reservationPast = Utils.createPastReservation(courtInserted.id, userInserted.id)
        reservationDao.save(reservationFuture)
        reservationDao.save(reservationPast)

        val reservationsList = reservationDao.getAll()
        reservationsList.forEach {
            val invitation = Utils.createInvitation(it.id, userInserted.id)
            invitationDao.save(invitation)
        }

        val futureInvitationList = invitationDao.getInvitationByEmailAndDate(userInserted.email, LocalDate.now().toString())
        assert(futureInvitationList.isNotEmpty())
        assert(futureInvitationList.size == 1)

    }

    @After
    fun clearAllDatabase() {
        db.clearAllTables()
    }


}