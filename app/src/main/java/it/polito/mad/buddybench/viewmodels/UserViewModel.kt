package it.polito.mad.buddybench.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.dto.UserDTO
import it.polito.mad.buddybench.repositories.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository
    private val _initName: String = ""

    private val _userName: MutableLiveData<String> = MutableLiveData(_initName)

    val username: LiveData<String> get() = _userName

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

    fun setUserName(name: String) {
        _userName.value = name
    }

}