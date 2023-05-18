package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.classes.Profile
import it.polito.mad.buddybench.persistence.entities.User
import java.time.LocalDate

class UserDTO(val name: String, val surname: String, val nickname: String, val birthdate: LocalDate, val location: String,
              val email: String, val reliability: Int, val imagePath: String?
) {

    fun toEntity(): User {
        return User(
            name = this.name,
            surname = this.surname,
            nickname = this.nickname,
            birthdate = this.birthdate.toString(),
            location = this.location,
            email = this.email,
            reliability = this.reliability,
            imagePath = this.imagePath
        )
    }

    fun toProfile(): Profile{
        return Profile(
            name = this.name,
            surname = this.surname,
            nickname = this.nickname,
            email = this.email,
            birthdate = this.birthdate,
            reliability = this.reliability,
            friends = mutableListOf(),
            imageUri = null,
            location = this.location,
            pendings = mutableListOf(),
            sports = mutableListOf()
        )
    }

}

