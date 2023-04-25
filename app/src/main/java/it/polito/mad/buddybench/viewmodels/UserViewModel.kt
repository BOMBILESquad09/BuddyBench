package it.polito.mad.buddybench.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.repositories.UserRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private val _profile: MutableLiveData<Profile> = MutableLiveData(
        Profile(
            "", "", "", "", "", LocalDate.now(), 0,
            Uri.parse(""), listOf()
        )
    )

    val profile: LiveData<Profile> get() = _profile

    fun updateUserInfo(profile: Profile) {
        userRepository.update(
            UserDTO(
                profile.name!!,
                profile.surname!!,
                profile.nickname!!,
                profile.birthdate,
                profile.location!!,
                profile.email,
                profile.reliability
            )
        )
    }

    fun updateProfile(profile: Profile) {
        _profile.value = profile
    }


}