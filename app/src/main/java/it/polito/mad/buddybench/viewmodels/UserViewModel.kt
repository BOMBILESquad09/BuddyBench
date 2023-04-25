package it.polito.mad.buddybench.viewmodels

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.entities.UserWithSports
import it.polito.mad.buddybench.entities.UserWithSportsDTO
import it.polito.mad.buddybench.repositories.UserRepository
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

    fun getUser(email: String): Profile{
        val u = userRepository.getUser(email);
        println("imagePath")
        println(u.user.imagePath == "null")
        val uri = if (u.user.imagePath == null || u.user.imagePath=="null"||u.user.imagePath=="" )
            Uri.parse("null") else
            Uri.parse(u.user.imagePath)

        return Profile(
            u.user.name,
            u.user.surname,
            u.user.nickname,
            u.user.email,
            u.user.location,
            u.user.birthdate,
            u.user.reliability,
            uri,
            u.sports
        )

    }

    fun updateUserInfo(profile: Profile) {
        userRepository.update(
            UserWithSportsDTO(
                profile.toUserDto(),
                profile.sports
            )
        )
    }

    fun setUserName(name: String) {
        _userName.value = name
    }

}