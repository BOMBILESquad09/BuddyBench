package it.polito.mad.buddybench.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import java.util.*

class Utils {
    companion object {
        fun capitalize(string: String): String {
            return string.lowercase(Locale.ENGLISH)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }
        }
    }
}