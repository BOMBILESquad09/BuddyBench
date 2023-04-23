package it.polito.mad.buddybench.dto

import it.polito.mad.buddybench.entities.Sport

class SportDTO(val name: String) {


    fun toEntity(): Sport {
        return Sport (
            sport_name = this.name
        )
    }
}

