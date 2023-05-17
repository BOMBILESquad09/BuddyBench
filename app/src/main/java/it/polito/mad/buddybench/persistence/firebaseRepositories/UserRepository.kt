package it.polito.mad.buddybench.persistence.firebaseRepositories

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.ProfileData
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.UserDTO
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserRepository {
    val db = FirebaseFirestore.getInstance()

    fun getUser(email: String, user: MutableLiveData<Profile>) {
        db.collection("users").
        document(email).get()
            .addOnSuccessListener {
                if(it.data != null) {
                    user.value = serializeUser(it.data as Map<String, Object>)
                }
                else{
                    val newProfile = createProfile()
                    db.collection("users").document(newProfile.email).set(
                        newProfile
                    ).addOnSuccessListener {
                        user.value = Profile(
                            newProfile.name,
                            newProfile.surname,
                            newProfile.nickname,
                            newProfile.email,
                            newProfile.location,
                            LocalDate.parse(newProfile.birthdate, DateTimeFormatter.ISO_LOCAL_DATE),
                            newProfile.reliability,
                            null,
                            newProfile.sports
                        )
                    }
                }
            }.addOnFailureListener {
                println("not exists")
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
                    Skills.NEWBIE,11,11, mutableListOf("Coppa Champion")
                )
            )
        )
    }

    fun uploadProfileImage() {

    }

    fun save(user: UserDTO) {
        TODO()
    }

    fun checkUser(email: String): UserWithSports? {
        TODO()
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
            null,
            user.sports
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
        val profile = Profile.fromJSON(JSONObject( sharedPreferences.getString("profile", Profile.mockJSON())!!))
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

    companion object{
        fun serializeUser(map: Map<String, Object>): Profile{
            val sports = mutableListOf<Sport>()
            val fbSports = map["sports"] as List<Map<String, Any>>
            for (s in fbSports){
                val name = Sports.valueOf( s["name"] as String)
                val skill = Skills.valueOf(s["skill"] as String)
                val matchesPlayed = s["matchesPlayed"] as Long
                val matchesOrganized = s["matchesOrganized"] as Long
                val achievements = s["achievements"] as MutableList<String>
                sports.add(Sport(name, skill, matchesPlayed.toInt(), matchesOrganized.toInt(),
                    achievements
                ))

            }
            return Profile(
                name = map["name"] as String,
                surname = map["surname"] as String,
                nickname = map["nickname"] as String,
                email = map["email"] as String,
                location = map["location"] as String,
                birthdate = LocalDate.parse(map["birthdate"] as String, DateTimeFormatter.ISO_LOCAL_DATE),
                reliability = (map["reliability"] as Long).toInt(),
                imageUri = null,
                sports = sports
            )
        }
    }
}