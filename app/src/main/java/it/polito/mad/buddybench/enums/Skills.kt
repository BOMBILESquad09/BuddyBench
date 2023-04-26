package it.polito.mad.buddybench.enums

import it.polito.mad.buddybench.R

enum class Skills {
    NULL,
    NEWBIE,
    AMATEUR,
    SKILLED,
    PRO,
    WORLD_CLASS;

    companion object {
        /**
         * Maps a skill level to a color resource id
         *
         * @param skill the skill level
         * @return The color as (R.color.value)
         */
        fun skillToColor(skill: Skills): Int {
            // TODO: Define custom color for each skill level
            return when (skill) {
                NEWBIE -> R.color.md_theme_dark_error
                AMATEUR -> R.color.md_theme_dark_background
                SKILLED -> R.color.md_theme_dark_background
                PRO -> R.color.md_theme_dark_error
                WORLD_CLASS -> R.color.md_theme_dark_background
                NULL -> R.color.md_theme_dark_error
            }
        }

        fun toJSON(skill: Skills): String {
            return when (skill) {
                NEWBIE -> "NEWBIE"
                AMATEUR -> "AMATEUR"
                SKILLED -> "SKILLED"
                PRO -> "PRO"
                WORLD_CLASS -> "WORLD_CLASS"
                NULL -> "NULL"
            }
        }

        fun fromJSON(skill: String): Skills? {
            return when (skill.uppercase()) {
                "NEWBIE" -> NEWBIE
                "AMATEUR" -> AMATEUR
                "SKILLED" -> SKILLED
                "PRO" -> PRO
                "WORLD_CLASS" -> WORLD_CLASS
                "WORLD CLASS" -> WORLD_CLASS
                "NULL" -> NULL
                else -> null
            }
        }
    }


}