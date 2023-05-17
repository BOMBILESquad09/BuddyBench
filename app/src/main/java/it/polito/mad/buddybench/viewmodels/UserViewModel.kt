package it.polito.mad.buddybench.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import it.polito.mad.buddybench.persistence.firebaseRepositories.ImageRepository
import it.polito.mad.buddybench.persistence.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val userRepositoryFirebase = it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository()
    private val imageRepository: ImageRepository = ImageRepository()

    private val _initName: String = ""

    private val _userName: MutableLiveData<String> = MutableLiveData(_initName)
    private val _user: MutableLiveData<Profile> = MutableLiveData(null)
    private val _profileImage: MutableLiveData<Uri> = MutableLiveData(null)

    val user: LiveData<Profile> = _user
    val profileImage: LiveData<Uri> get() = _profileImage

    var oldSports: List<Sport> = listOf()
    private var _invisibleSports: MutableList<Sport> = mutableListOf()
    private val _sports: MutableLiveData<MutableList<Sport>> = MutableLiveData(null)

    var oldAchievements:List<String> = listOf()

    val sports: LiveData<MutableList<Sport>> = _sports
    val username: LiveData<String> get() = _userName

    fun checkUserEmail(email: String): UserWithSports? {
        val u = userRepository.checkUser(email);
        return u
    }


    fun getUser(email: String): LiveData<Profile> {
        userRepositoryFirebase.getUser(email, _user)

        /*Thread {
            val u = userRepository.getUser(email);
            val uri =
                if (u.user.imagePath == null || u.user.imagePath == "null" || u.user.imagePath == "")
                    Uri.parse("null") else
                    Uri.parse(u.user.imagePath)
            _user.postValue(
                Profile(
                    u.user.name,
                    u.user.surname,
                    u.user.nickname,
                    u.user.email,
                    u.user.location,
                    u.user.birthdate,
                    u.user.reliability,
                    uri,
                    u.sports.toMutableList()
                )
            )
        }.start()*/

        return user
    }

    fun updateUserInfo(profile: Profile, oldEmail: String) {
        userRepositoryFirebase.update(profile, _user)

        /*
        _user.value = profile
        _userName.value = profile.name!!
        Thread {
            userRepository.update(
                UserWithSportsDTO(
                    profile.toUserDto(),
                    profile.sports
                ), oldEmail
            )
        }.start()*/

    }

    fun uploadProfileImage(uri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        imageRepository.uploadImageToPath(uri, "${userId}.jpg", { onImageUploadSuccess() },{ onImageUploadError()} )
    }

    fun getProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val destinationUri = Uri.parse("/profile_image/${userId}.jpg")
        imageRepository.getUserProfileImage(userId!!, destinationUri ,{ _profileImage.postValue(destinationUri) }, { throw Error("Error downloading profile image")})
    }

    private fun onImageUploadSuccess() {
        println("Image uploaded")
    }

    private fun onImageUploadError() {
        println("Image upload error")
    }

    fun setUserName(name: String) {
        Thread {
            _userName.value = name
        }.start()
    }


    fun setSports(sportsList: List<Sport>): LiveData<MutableList<Sport>>{
        _invisibleSports = sportsList.filter { it.skill == Skills.NULL }.toMutableList()
        _sports.value = sportsList.filter { it.skill != Skills.NULL }.toMutableList()
        oldSports = _sports.value!!.toList()
        return sports
    }

    fun removeSport(sport: Sport):  LiveData<MutableList<Sport>>{
        oldSports = _sports.value!!.toList()
        _sports.value = _sports.value!!.filter {
            (it.name != sport.name)
        }.toMutableList()

        _invisibleSports.add(sport)
        return sports
    }

    fun updateSport(sport: Sport):  LiveData<MutableList<Sport>>{
        oldSports = _sports.value!!.map { it.copy() }

        _sports.value = _sports.value!!.map {
            if(it.name == sport.name){sport}
            else
            it
        }.toMutableList()

        return sports
    }

    fun addSport(sport: Sport):  LiveData<MutableList<Sport>>{

        oldSports = _sports.value!!.map { it.copy() }
        val exists = _invisibleSports!!.find{ it.name == sport.name}
        _invisibleSports.remove(exists)
        if (exists != null){
            _sports.value = _sports.value!!.plus(exists).toMutableList()
        } else {
            _sports.value = _sports.value!!.plus(sport).toMutableList()
        }

        return sports
    }

    fun addAchievement(sport: Sport,achievement: String){
        oldAchievements = _sports.value!!.find { it.name == sport.name }!!.achievements
        oldSports = _sports.value!!.map { it.copy() }
        _sports.value = _sports.value!!.map {
            if (it.name == sport.name){
                it.achievements.add(achievement)
                it
            } else  {
            it}
        }.toMutableList()
    }

    fun removeAchievement(sport: Sport, achievement: String){
        oldSports = _sports.value!!.map { it.copy() }
        _sports.value = _sports.value!!.map {
            if (it.name == sport.name){
                it.achievements.remove(achievement)
                it
            } else {
            it
            }
        }.toMutableList()
        println(achievement)
        println(_sports.value!![0].achievements.size)
    }
}