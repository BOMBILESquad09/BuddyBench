package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.ProfileData
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserRepository {
    val db = FirebaseFirestore.getInstance()

    suspend fun getUser(email: String, callback: (Profile) -> Unit) {
        withContext(Dispatchers.IO){
            val profile = db.collection("users").document(email).get().await()
            if(profile.data != null){
                val serializedProfile = serializeUser( profile.data as Map<String, Any>)
                (profile.data!!["friends"] as List<DocumentReference>).map { it.get() }.map { it.await() }.forEach{
                    serializedProfile.friends.add(serializeUser(it.data as Map<String, Any>)) }
                (profile.data!!["friend_requests_pending"] as List<DocumentReference>).map { it.get() }.map { it.await() }.forEach{
                    serializedProfile.pendings.add(serializeUser(it.data as Map<String, Any>)) }
                callback(serializedProfile)
            } else{
                val newProfile = createProfile()
                val x = db.collection("users").document(newProfile.email).set(
                    newProfile
                ).await()
                callback(Profile(
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
                ))
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
            "", mutableListOf(
                Sport(
                    Sports.TENNIS,
                    Skills.NEWBIE, 11, 11, mutableListOf("Coppa Champion")
                )
            ), listOf(), listOf()
        )
    }

    fun update(user: Profile, wrapper: MutableLiveData<Profile>) {
        val profileData = ProfileData(
            user.name,
            user.surname,
            user.nickname,
            user.email,
            user.location,
            user.birthdate.toString(),
            user.reliability,
            user.imageUri.toString(),
            user.sports,
            user.friends.map { db.document("users/${it.email}") },
            user.pendings.map { db.document("users/${it.email}") }
        )
        db.collection("users")
            .document(user.email).set(profileData).addOnSuccessListener {
                wrapper.value = user
            }

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
        fun serializeUser(map: Map<String, Any>): Profile {
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
                pendings = mutableListOf()
            )
        }
    }
}