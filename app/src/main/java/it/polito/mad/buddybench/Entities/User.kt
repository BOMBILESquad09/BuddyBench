package it.polito.mad.buddybench.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
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
    val reliability: Float

)