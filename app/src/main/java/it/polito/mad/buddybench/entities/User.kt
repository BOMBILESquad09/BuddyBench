package it.polito.mad.buddybench.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.dto.UserDTO
import java.time.LocalDate

@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "surname")
    val surname: String,

    @ColumnInfo(name = "nickname")
    val nickname: String,

    @ColumnInfo(name = "birthdate")
    val birthdate: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "reliability")
    val reliability: Int

)

fun User.toUserDTO(): UserDTO {
    return UserDTO(
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        birthdate = LocalDate.parse(this.birthdate),
        location = this.location,
        email = this.email,
        reliability = this.reliability
    )
}