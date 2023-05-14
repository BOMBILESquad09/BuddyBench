package it.polito.mad.buddybench.persistence.entities

import android.media.Rating
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "review", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Court::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("court"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class Review(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "review_id")
    val id: Int = 0,

    @ColumnInfo(name="description")
    val description: String = "",

    @ColumnInfo(name="date")
    val date: String = "",

    @ColumnInfo(name = "review_rating")
    val rating: Int = 0,

    @ColumnInfo(name = "user")
    val userId: Int = 0,

    @ColumnInfo(name= "court")
    val courtId: Int = 0
)


