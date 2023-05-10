package it.polito.mad.buddybench.viewmodels

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import it.polito.mad.buddybench.persistence.entities.UserWithSportsDTO

import it.polito.mad.buddybench.persistence.repositories.UserRepository
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository
    private val _initName: String = ""

    private val _userName: MutableLiveData<String> = MutableLiveData(_initName)
    private val _user: MutableLiveData<Profile> = MutableLiveData(null)
    private val user: LiveData<Profile> = _user

    var oldSports: List<Sport> = listOf()
    private var _invisibleSports: MutableList<Sport> = mutableListOf()
    private val _sports: MutableLiveData<MutableList<Sport>> = MutableLiveData(null)


    val sports: LiveData<MutableList<Sport>> = _sports


    val username: LiveData<String> get() = _userName


    fun checkUserEmail(email: String): UserWithSports? {
        val u = userRepository.checkUser(email);
        return u
    }


    fun getUser(email: String): LiveData<Profile> {
        Thread {
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
        }.start()

        return user
    }

    fun updateUserInfo(profile: Profile, oldEmail: String) {
        _user.value = profile
        _userName.value = profile.name!!
        Thread {
            userRepository.update(
                UserWithSportsDTO(
                    profile.toUserDto(),
                    profile.sports
                ), oldEmail
            )
        }.start()

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
        println("-------------updateeee-------------------------------")
        oldSports.forEach { println(it) }

        _sports.value = _sports.value!!.map {
            if(it.name == sport.name){sport}
            else
            it
        }.toMutableList()
        println()
        oldSports.forEach { println(it) }
        _sports.value!!.forEach { println(it) }
        println("------------------")

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
        println(sports.value!!.size)

        return sports
    }

}