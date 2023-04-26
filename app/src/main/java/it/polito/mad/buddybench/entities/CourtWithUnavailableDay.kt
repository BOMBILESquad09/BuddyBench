package it.polito.mad.buddybench.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CourtWithUnavailableDay (

    @Embedded val court: Court,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = UnavailableDayCourt::class
    )
    val unavailableDays: List<CourtWithUnavailableDay>
)