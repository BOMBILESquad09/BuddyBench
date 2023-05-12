package it.polito.mad.buddybench.activities.profile

import androidx.recyclerview.widget.DiffUtil
import it.polito.mad.buddybench.classes.Sport
import it.polito.mad.buddybench.persistence.dto.CourtDTO

class AchievementDiffUtils(
    private val lastAchievement: List<String>,
    private val newAchievement: List<String>

): DiffUtil.Callback(
){
    override fun getOldListSize(): Int {
        return lastAchievement.size
    }

    override fun getNewListSize(): Int {
        return newAchievement.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastAchievement[oldItemPosition] == newAchievement[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return lastAchievement[oldItemPosition] == newAchievement[newItemPosition]


    }

}