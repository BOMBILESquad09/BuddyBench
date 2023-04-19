package it.polito.mad.buddybench.Entities

import androidx.annotation.NonNull
import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtDTO

@Entity(
    tableName = "Court", foreignKeys = arrayOf(
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sport"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
    ), indices = [Index(value = ["courtName", "sport"], unique = true)]
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
    val sport: String,

    )

data class CourtWithSport (

    @Embedded val court: Court,
    @Relation(
        parentColumn = "sport",
        entityColumn = "id"
    )
    val sport: Sport
)

fun CourtWithSport.toCourtDTO(): CourtDTO {
    return CourtDTO(
        courtName = this.court.courtName,
        address = this.court.address,
        feeHour = this.court.feeHour,
        sport = this.sport.id
    )
}

fun Court.toCourtDTO(sportName: String): CourtDTO {
    return CourtDTO(
        courtName = this.courtName,
        address = this.address,
        feeHour = this.feeHour,
        sport = sportName
    )
}