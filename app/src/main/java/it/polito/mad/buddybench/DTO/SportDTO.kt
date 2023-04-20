package it.polito.mad.buddybench.DTO

import it.polito.mad.buddybench.entities.Sport

class SportDTO(sportName: String) {
    val sportName = sportName

}

fun SportDTO.toEntity(): Sport {
    return Sport (
        sportName = this.sportName
    )
}