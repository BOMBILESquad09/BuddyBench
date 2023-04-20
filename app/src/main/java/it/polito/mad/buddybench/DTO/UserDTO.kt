package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.entities.User
import java.time.LocalDate

class UserDTO(name: String, surname: String, nickname: String, birthdate: LocalDate, location: String, email: String, reliability: Int) {

    val name = name
    val surname = surname
    val nickname = nickname
    val birthdate = birthdate
    val location = location
    val email = email
    val reliability = reliability

}

fun UserDTO.toEntity(): User {
    return User(
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        birthdate = this.birthdate.toString(),
        location = this.location,
        email = this.email,
        reliability = this.reliability,
    )
}