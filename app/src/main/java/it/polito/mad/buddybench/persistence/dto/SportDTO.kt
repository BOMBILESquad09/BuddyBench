package it.polito.mad.buddybench.persistence.dto

import it.polito.mad.buddybench.persistence.entities.Sport

class SportDTO(val name: String) {


    fun toEntity(): Sport {
        return Sport (
            sport_name = this.name
        )
    }
}

