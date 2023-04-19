package it.polito.mad.buddybench.Entities

import androidx.annotation.NonNull
import androidx.room.*
import it.polito.mad.buddybench.DTO.CourtDTO

@Entity(
    tableName = "Court", foreignKeys = arrayOf(
        ForeignKey(
            entity = Sport::class,
            parentColumns = arrayOf("name"),
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
    val name: String,

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
        entityColumn = "name"
    )
    val sport: Sport
)

fun CourtWithSport.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.court.name,
        address = this.court.address,
        feeHour = this.court.feeHour,
        sport = this.sport.name
    )
}

fun Court.toCourtDTO(): CourtDTO {
    return CourtDTO(
        name = this.name,
        address = this.address,
        feeHour = this.feeHour,
        sport = this.sport
    )
}