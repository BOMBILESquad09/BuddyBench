package it.polito.mad.buddybench.activities.findcourt

import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.dto.CourtDTO

class CourtsDiffUtils(
    private val lastCourts: List<CourtDTO>,
    private val newCourts: List<CourtDTO>

): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastCourts.size
    }

    override fun getNewListSize(): Int {
        return newCourts.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastCourts[oldItemPosition].name == newCourts[newItemPosition].name &&
                lastCourts[oldItemPosition].sport == newCourts[newItemPosition].sport
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastCourts[oldItemPosition].name == newCourts[newItemPosition].name &&
                lastCourts[oldItemPosition].sport == newCourts[newItemPosition].sport
    }

}