package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.ui.text.substring
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.ProfileData
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserSport
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import it.polito.mad.buddybench.persistence.entities.UserWithSportsDTO
import it.polito.mad.buddybench.persistence.entities.toUserDTO
import it.polito.mad.buddybench.persistence.entities.toUserSportDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Objects

class UserRepository {
    val db = FirebaseFirestore.getInstance()


    suspend fun getUser(email: String, callback: (Profile) -> Unit) {
        withContext(Dispatchers.IO) {
            val profile = db.collection("users").document(email).get().await()
            if (profile.data != null) {
                val serializedProfile = serializeUser(profile.data as Map<String, Object>)
                (profile.data!!["friends"] as List<DocumentReference>).map { it.get() }
                    .map { it.await() }.forEach {
                    serializedProfile.friends.add(serializeUser(it.data as Map<String, Object>))
                }
                (profile.data!!["friend_requests_pending"] as List<DocumentReference>).map { it.get() }
                    .map { it.await() }.forEach {
                    serializedProfile.pendings.add(serializeUser(it.data as Map<String, Object>))
                }

                if (profile.data!!["last_update"] == null || LocalDate.parse(profile.data!!["last_update"] as String, DateTimeFormatter.ISO_LOCAL_DATE) != LocalDate.now()) {
                    val organized = db.collection("reservations")
                        .whereEqualTo("user", db.document("users/$email")).get()
                    val invited = db.collection("reservations")
                        .whereArrayContains("accepted", db.document("users/$email")).get()
                    organized.await()
                    invited.await()
                    val counters: HashMap<Sports, Pair<Int, Int>> = HashMap()
                    for (o in organized.result) {
                        if(!checkReservationIsPassed(o.data)) continue
                        val sport = Sports.valueOf(
                            (o.data!!["court"] as DocumentReference).id.split("_").last()
                        )
                        if (counters[sport] == null) {
                            counters[sport] = Pair(1, 1)
                        } else {
                            counters[sport] =
                                Pair(counters[sport]!!.first + 1, counters[sport]!!.second + 1)
                        }
                    }
                    for (i in invited.result) {
                        if(!checkReservationIsPassed(i.data)) continue
                        val sport = Sports.valueOf(
                            (i.data!!["court"] as DocumentReference).id.split("_").last()
                        )
                        if (counters[sport] == null) {
                            counters[sport] = Pair(0, 1)
                        } else {
                            counters[sport] =
                                Pair(counters[sport]!!.first, counters[sport]!!.second + 1)
                        }
                    }

                    serializedProfile.sports.forEach {
                        it.matchesOrganized = counters[it.name]?.first ?: 0
                        it.matchesPlayed = counters[it.name]?.second ?: 0
                    }
                    update(serializedProfile, callback)

                    return@withContext
                }
                callback(serializedProfile)
            } else {
                val newProfile = createProfile()
                val x = db.collection("users").document(newProfile.email).set(
                    newProfile
                ).await()
                callback(
                    Profile(
                        newProfile.name,
                        newProfile.surname,
                        newProfile.nickname,
                        newProfile.email,
                        newProfile.location,
                        LocalDate.parse(
                            newProfile.birthdate,
                            DateTimeFormatter.ISO_LOCAL_DATE
                        ),
                        newProfile.reliability,
                        null,
                        newProfile.sports,
                        mutableListOf(),
                        mutableListOf()
                    )
                )
            }
        }
    }

    private fun createProfile(): ProfileData {
        val user = Firebase.auth.currentUser!!
        val name = user.displayName!!.substringBefore(" ")
        val surname = user.displayName!!.substringAfter(" ")
        return ProfileData(
            name, surname, "BuddyBenchGuest", user.email!!,
            "Turin", LocalDate.of(1999, 4, 27).toString(), 80,
            "", mutableListOf()
            , listOf(), listOf(), LocalDate.now()
        )
    }


    suspend fun update(user: Profile, callback: (Profile) -> Unit) {
        withContext(Dispatchers.IO) {
            db.collection("users")
                .document(user.email).update(
                    mapOf(
                        "name" to user.name,
                        "surname" to user.surname,
                        "nickname" to user.nickname,
                        "location" to user.location,
                        "birthdate" to user.birthdate.toString(),
                        "sports" to user.sports,
                        "imageUri" to user.imageUri.toString(),
                        "last_update" to LocalDate.now().toString()
                    )
                ).await()
            callback(user)
        }

    }

    fun checkReservationIsPassed(reservation: Map<String, Any>): Boolean{
        val date = LocalDate.parse(reservation["date"] as String, DateTimeFormatter.ISO_LOCAL_DATE)
        if (date < LocalDate.now()) return true
        if(date == LocalDate.now()) return (reservation["endTime"] as Long).toInt() < LocalTime.now().hour
        return false
    }

    /**
     * TODO: Update with user session (auth)
     * Get current user from shared preferences (if any)
     */
    fun getCurrentUser(sharedPreferences: SharedPreferences): UserDTO {
        val profile = Profile.fromJSON(
            JSONObject(
                sharedPreferences.getString(
                    "profile",
                    Profile.mockJSON()
                )!!
            )
        )
        val name = profile.name ?: "Name"
        val surname = profile.surname ?: "Surname"
        val email = profile.email
        val nickname = profile.nickname ?: "Nickname"
        val location = profile.location ?: "Roma"
        val reliability = profile.reliability
        val imagePath = profile.imageUri.toString()
        val birthdate = profile.birthdate
        return UserDTO(name, surname, nickname, birthdate, location, email, reliability, imagePath)
    }

    companion object {
        fun serializeUser(map: Map<String, Object>): Profile {
            val sports = mutableListOf<Sport>()
            val fbSports = map["sports"] as List<Map<String, Any>>
            for (s in fbSports) {
                val name = Sports.valueOf(s["name"] as String)
                val skill = Skills.valueOf(s["skill"] as String)
                val matchesPlayed = s["matchesPlayed"] as Long
                val matchesOrganized = s["matchesOrganized"] as Long
                val achievements = s["achievements"] as MutableList<String>
                sports.add(
                    Sport(
                        name, skill, matchesPlayed.toInt(), matchesOrganized.toInt(),
                        achievements
                    )
                )
            }


            return Profile(
                name = map["name"] as String,
                surname = map["surname"] as String,
                nickname = map["nickname"] as String,
                email = map["email"] as String,
                location = map["location"] as String,
                birthdate = LocalDate.parse(
                    map["birthdate"] as String,
                    DateTimeFormatter.ISO_LOCAL_DATE
                ),
                reliability = (map["reliability"] as Long).toInt(),
                imageUri = null,
                sports = sports,
                friends = mutableListOf(),
                pendings = mutableListOf(),
                isFriend = (map["friends"] as List<DocumentReference>).any { it.id == Firebase.auth.currentUser!!.email },
                isPending = (map["friend_requests_pending"] as List<DocumentReference>).any { it.id == Firebase.auth.currentUser!!.email }
            )
        }
    }
}