package it.polito.mad.buddybench.enums

import android.content.Context
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import it.polito.mad.buddybench.R
import java.util.*

enum class Sports {
    FOOTBALL,
    BASKETBALL,
    TENNIS,
    VOLLEYBALL;

    companion object {
        fun sportToIconDrawableAlternative(sport: Sports): Int {
            return when (sport) {
                FOOTBALL -> R.drawable.football
                TENNIS -> R.drawable.tennis
                BASKETBALL -> R.drawable.basketball
                VOLLEYBALL -> R.drawable.volleyball
            }
        }

        fun sportToIconDrawable(sport: Sports): Int {
            return when (sport) {
                FOOTBALL -> R.drawable.football_png
                TENNIS -> R.drawable.tennis_png
                BASKETBALL -> R.drawable.basketball_png
                VOLLEYBALL -> R.drawable.volleyball_png
            }
        }

        fun toJSON(sport: Sports): String {

            return when (sport) {
                FOOTBALL -> "FOOTBALL"
                TENNIS -> "TENNIS"
                BASKETBALL -> "BASKETBALL"
                VOLLEYBALL -> "VOLLEYBALL"
            }.lowercase()
        }

        fun fromJSON(sport: String): Sports? {
            return when (sport.uppercase()) {
                "FOOTBALL" -> FOOTBALL
                "TENNIS" -> TENNIS
                "BASKETBALL" -> BASKETBALL
                "VOLLEYBALL" -> VOLLEYBALL
                else -> null
            }
        }

        fun getStringValues(excludeSports: List<Sports> = listOf()): Array<CharSequence> {
            val valuesArray = mutableListOf<CharSequence>()
            for (sport in Sports.values()) {
                if (excludeSports.indexOf(sport) != -1) continue
                valuesArray.add(sport.toString().lowercase().replaceFirstChar { it.uppercase() })
            }

            return valuesArray.toTypedArray()
        }

        fun getSportColor(sport: Sports, context: Context): Int {
            return when (sport) {
                TENNIS -> context.getColor(R.color.tennis_sport)
                FOOTBALL -> context.getColor(R.color.football_sport)
                BASKETBALL -> context.getColor(R.color.basketball_sport)
                VOLLEYBALL -> context.getColor(R.color.volleyball_sport)
            }
        }


    }


}