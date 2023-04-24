package it.polito.mad.buddybench.viewmodels

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

}