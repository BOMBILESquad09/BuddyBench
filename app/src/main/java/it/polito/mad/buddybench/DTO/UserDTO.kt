package it.polito.mad.buddybench.DTO

import android.provider.ContactsContract.CommonDataKinds.Nickname
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.Entities.User
import java.time.LocalDate

class UserDTO(val name: String, val surname: String, val nickname: String, val birthdate: LocalDate, val location: String,
              val email: String, val reliability: Int) {

    fun toEntity(): User {
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

}

