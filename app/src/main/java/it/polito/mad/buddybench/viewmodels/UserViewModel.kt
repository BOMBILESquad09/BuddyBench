package it.polito.mad.buddybench.viewmodels

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
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

    val username: LiveData<String> get() = _userName

    fun getUser(email: String): LiveData<Profile>{
        Thread{
            val u = userRepository.getUser(email);
            val uri = if (u.user.imagePath == null || u.user.imagePath=="null"||u.user.imagePath=="" )
                Uri.parse("null") else
                Uri.parse(u.user.imagePath)
            _user.postValue(  Profile(
                u.user.name,
                u.user.surname,
                u.user.nickname,
                u.user.email,
                u.user.location,
                u.user.birthdate,
                u.user.reliability,
                uri,
                u.sports
            ))
        }.start()


        return user

    }

    fun updateUserInfo(profile: Profile, oldEmail: String) {
        _user.value = profile
        _userName.value = profile.name!!
        Thread{
            userRepository.update(
                UserWithSportsDTO(
                    profile.toUserDto(),
                    profile.sports
                ), oldEmail
            )
        }.start()

    }

    fun setUserName(name: String) {
        Thread{
            _userName.value = name
        }.start()

    }

}