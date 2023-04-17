package it.polito.mad.buddybench.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Court", foreignKeys = arrayOf(
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sport"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class Court(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "courtName")
    val courtName: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "feeHour")
    val feeHour: Int,

    @ColumnInfo(name = "sport")
    val sport: Int,

    )