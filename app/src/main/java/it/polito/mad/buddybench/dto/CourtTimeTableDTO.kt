package it.polito.mad.buddybench.dto

import java.time.DayOfWeek
import java.time.LocalTime

class CourtTimeTableDTO(
    val court: CourtDTO,
    val timeTable: HashMap<DayOfWeek, Pair<LocalTime, LocalTime>>
)