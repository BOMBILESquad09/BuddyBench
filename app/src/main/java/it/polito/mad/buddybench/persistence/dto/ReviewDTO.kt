package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.classes.Profile
import java.time.LocalDate

class ReviewDTO (
    val user: Profile,
    val date: LocalDate,
    val rating: Int,
    val description: String,
    val courtDTO: CourtDTO
)




