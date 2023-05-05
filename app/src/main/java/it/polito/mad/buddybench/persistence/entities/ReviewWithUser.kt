package it.polito.mad.buddybench.persistence.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ReviewWithUser(
    @Embedded val review: Review,
    @Relation(
        parentColumn = "user",
        entityColumn = "id",
    )
    val user: User,
)