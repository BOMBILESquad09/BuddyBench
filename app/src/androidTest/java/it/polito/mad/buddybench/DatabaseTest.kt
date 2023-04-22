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

}