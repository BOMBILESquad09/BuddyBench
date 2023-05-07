package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.User
import java.time.LocalDate

class ReviewDTO (
    val user: UserDTO,
    val date: LocalDate,
    val rating: Int,
    val description: String,
    val courtDTO: CourtDTO
)

