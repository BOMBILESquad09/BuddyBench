package it.polito.mad.buddybench.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import it.polito.mad.buddybench.persistence.entities.Sport
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import java.util.regex.Pattern

@Entity(
    tableName = "court", foreignKeys = arrayOf(
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("sport_name"),
            childColumns = arrayOf("sport"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    )
)
data class Court(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "fee_hour")
    val feeHour: Int,

    @ColumnInfo(name = "sport")
    var sport: String,

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String,

    @ColumnInfo(name = "path")
    var path: String,

    @ColumnInfo(name = "fee_equipment")
    var feeEquipment: Int,

    @ColumnInfo(name = "rating")
    var rating: Double,

    @ColumnInfo(name = "n_reviews")
    var nReviews: Int


    )
fun Court.toCourtDTO(): CourtDTO {
    return CourtDTO(
        this.name,
        this.address,
        this.location,
        this.feeHour,
        this.sport,
        this.phoneNumber,
        this.path,
        this.feeEquipment,
        this.rating,
        this.nReviews,
        listOf()

    )
}