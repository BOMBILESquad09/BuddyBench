package it.polito.mad.buddybench.activities.profile

import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.persistence.dto.CourtDTO

class SportsDiffUtils(
    private val lastSports: List<Sport>,
    private val newSports: List<Sport>

): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastSports.size
    }

    override fun getNewListSize(): Int {
        return newSports.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastSports[oldItemPosition].name == lastSports[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastSports[oldItemPosition].skill == newSports[newItemPosition].skill &&
                newSports[oldItemPosition].name == lastSports[newItemPosition].name

    }

}