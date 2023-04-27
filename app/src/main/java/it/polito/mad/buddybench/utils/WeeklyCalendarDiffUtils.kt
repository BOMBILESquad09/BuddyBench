package it.polito.mad.buddybench.activities.findcourt

import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.persistence.dto.CourtDTO
import java.time.LocalDate

class WeeklyCalendarDiffUtils(
    private val lastCourts: List<Pair<LocalDate, Boolean>>,
    private val newCourts: List<Pair<LocalDate, Boolean>>

): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastCourts.size
    }

    override fun getNewListSize(): Int {
        return newCourts.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastCourts[oldItemPosition].first == newCourts[newItemPosition].first
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastCourts[oldItemPosition].second == newCourts[newItemPosition].second
    }

}