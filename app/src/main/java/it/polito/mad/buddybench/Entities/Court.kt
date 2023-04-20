package it.polito.mad.buddybench.Entities

import androidx.annotation.NonNull
import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtDTO

@Entity(
    tableName = "court", foreignKeys = arrayOf(
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("name"),
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
    val sport: String,

    )
fun Court.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.name,
        address = this.address,
        location = this.location,
        feeHour = this.feeHour,
        sport = this.sport

    )
}