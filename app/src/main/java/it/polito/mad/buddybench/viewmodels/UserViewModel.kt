package it.polito.mad.buddybench.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.enums.Skills
import it.polito.mad.buddybench.enums.Sports
import it.polito.mad.buddybench.persistence.dto.ReservationDTO
import it.polito.mad.buddybench.persistence.entities.User
import it.polito.mad.buddybench.persistence.entities.UserWithSports
import it.polito.mad.buddybench.persistence.entities.UserWithSportsDTO
import it.polito.mad.buddybench.persistence.firebaseRepositories.FriendRepository
import it.polito.mad.buddybench.persistence.firebaseRepositories.InvitationsRepository

import it.polito.mad.buddybench.persistence.repositories.UserRepository
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val userRepositoryFirebase = it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository()
    private val invitationsRepository = InvitationsRepository()

    private val friendRepository = FriendRepository()

    private val _userName: MutableLiveData<String> = MutableLiveData(null)
    private val _user: MutableLiveData<Profile> = MutableLiveData(null)
    val user: LiveData<Profile> = _user

    var oldSports: List<Sport> = listOf()
    private var _invisibleSports: MutableList<Sport> = mutableListOf()
    private val _sports: MutableLiveData<MutableList<Sport>> = MutableLiveData(null)

    var oldAchievements: List<String> = listOf()

    val sports: LiveData<MutableList<Sport>> = _sports

    lateinit var sharedPref: SharedPreferences



    fun checkUserEmail(email: String): UserWithSports? {
        val u = userRepository.checkUser(email);
        return u
    }


    fun getUser(email: String): LiveData<Profile> {
        runBlocking {
                userRepositoryFirebase.getUser(email) {
                    _user.postValue(it)
                }
        }
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

    fun setUserName(name: String) {
        _userName.value
    }


    fun setSports(sportsList: List<Sport>): LiveData<MutableList<Sport>> {
        _invisibleSports = sportsList.filter { it.skill == Skills.NULL }.toMutableList()
        _sports.value = sportsList.filter { it.skill != Skills.NULL }.toMutableList()
        oldSports = _sports.value!!.toList()
        return sports
    }

    fun removeSport(sport: Sport): LiveData<MutableList<Sport>> {
        oldSports = _sports.value!!.toList()


        _sports.value = _sports.value!!.filter {
            (it.name != sport.name)
        }.toMutableList()

        _invisibleSports.add(sport)
        return sports
    }

    fun updateSport(sport: Sport): LiveData<MutableList<Sport>> {
        oldSports = _sports.value!!.map { it.copy() }

        _sports.value = _sports.value!!.map {
            if (it.name == sport.name) {
                sport
            } else
                it
        }.toMutableList()


        return sports
    }

    fun addSport(sport: Sport): LiveData<MutableList<Sport>> {

        oldSports = _sports.value!!.map { it.copy() }
        val exists = _invisibleSports!!.find { it.name == sport.name }
        _invisibleSports.remove(exists)
        if (exists != null) {
            _sports.value = _sports.value!!.plus(exists).toMutableList()
        } else {
            _sports.value = _sports.value!!.plus(sport).toMutableList()
        }

        return sports
    }

    fun addAchievement(sport: Sport, achievement: String) {
        oldAchievements = _sports.value!!.find { it.name == sport.name }!!.achievements
        oldSports = _sports.value!!.map { it.copy() }
        _sports.value = _sports.value!!.map {
            if (it.name == sport.name) {
                it.achievements.add(achievement)
                it
            } else {
                it
            }
        }.toMutableList()


    }

    fun removeAchievement(sport: Sport, achievement: String) {
        oldSports = _sports.value!!.map { it.copy() }
        _sports.value = _sports.value!!.map {
            if (it.name == sport.name) {
                it.achievements.remove(achievement)
                it
            } else {
                it
            }
        }.toMutableList()
    }

}