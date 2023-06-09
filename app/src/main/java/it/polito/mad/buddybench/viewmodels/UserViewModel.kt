package it.polito.mad.buddybench.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
import it.polito.mad.buddybench.persistence.firebaseRepositories.UserRepository
import it.polito.mad.buddybench.utils.Utils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {


    @Inject
    lateinit var userRepositoryFirebase: UserRepository
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
    var onFailure = {}

    fun getUser(email: String = Firebase.auth.currentUser!!.email!!): LiveData<Profile> {

        viewModelScope.launch {
            userRepositoryFirebase.getUser(email, {
                onFailure()
            }
            ) {
                _user.postValue(it.copy())
            }
        }
        return user
    }

    fun fromSharedPreferences() {

        _user.value = (Profile.fromJSON(
            JSONObject(
                sharedPref.getString(
                    "profile",
                    Profile.mockJSON()
                )!!
            )
        ))
    }

//    fun updateUserInfo(profile: Profile) {
//
//        runBlocking {
//            userRepositoryFirebase.update(profile) {
//                _user.postValue(it)
//            }
//        }
//
//    }

    fun updateUserInfo(profile: Profile, onFailure: () -> Unit, onSuccess: () -> Unit) {

        userRepositoryFirebase.update(profile, onFailure) {
            _user.postValue(it)
            onSuccess()

        }


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

    fun getAllSports(): List<Sport> {
        val allSports = mutableListOf<Sport>()
        _invisibleSports.forEach { it.skill = Skills.NULL;allSports.add(it) }
        sports.value!!.forEach { allSports.add(it) }
        return allSports
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
        val exists = _invisibleSports.find { it.name == sport.name }
        _invisibleSports.remove(exists)
        if (exists != null) {
            exists.skill = Skills.NEWBIE
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

    fun sendFriendRequest(onSuccess: (Profile) -> Unit) {
        viewModelScope.launch {
            friendRepository.postFriendRequest(user.value!!.email, onFailure) {
                val p = user.value!!.copy()
                p.isPending = true
                _user.postValue(p)
                onSuccess(p)
            }
        }
    }

    fun removeFriendRequest(onSuccess: (Profile) -> Unit) {
        runBlocking {
            friendRepository.removeFriendRequest(user.value!!.email, onFailure) {
                val p = user.value!!.copy()
                p.isPending = false
                _user.postValue(p)
                onSuccess(p)
            }

        }

    }

    fun acceptFriendRequest(onSuccess: (Profile) -> Unit) {
        viewModelScope.launch {
            friendRepository.acceptFriendRequest(user.value!!.email, onFailure) {
                val p = user.value!!.copy()
                p.isRequesting = false
                p.isFriend = true
                _user.postValue(p)
                onSuccess(p)
            }

        }

    }

    fun removeFriend(onFailure: () -> Unit,onSuccess: (Profile) -> Unit) {
            friendRepository.removeFriend(user.value!!.email,{
               onFailure()
               this.onFailure()
            }
            ) {
                val p = user.value!!.copy()
                p.isFriend = false
                p.isPending = false
                _user.postValue(p)
                onSuccess(p)
            }



    }

}