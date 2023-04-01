package it.polito.mad.buddybench.enums

import it.polito.mad.buddybench.R

enum class Sports {
    FOOTBALL,
    BASKETBALL,
    TENNIS,
    VOLLEYBALL;

    companion object {
        fun sportToIconDrawable(sport: Sports): Int {
            return when (sport) {
                FOOTBALL -> R.drawable.football
                TENNIS -> R.drawable.tennis
                BASKETBALL -> R.drawable.basketball
                VOLLEYBALL -> R.drawable.volleyball
            }
        }

        fun toJSON(sport: Sports): String {

            return when (sport) {
                FOOTBALL -> "FOOTBALL"
                TENNIS -> "TENNIS"
                BASKETBALL -> "BASKETBALL"
                VOLLEYBALL -> "VOLLEYBALL"
            }
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
                valuesArray.add(sport.toString())
            }

            return valuesArray.toTypedArray()
        }


        fun fromIntToEnum(value: Int): Sports? {
            return when(value) {
                0 -> FOOTBALL
                1 -> BASKETBALL
                2 -> TENNIS
                3 -> VOLLEYBALL
                else -> null
            }
        }
    }


}