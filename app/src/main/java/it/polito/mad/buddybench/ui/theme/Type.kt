package it.polito.mad.buddybench.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.polito.mad.buddybench.R

val GothamFont = FontFamily(
    Font(R.font.gotham_bold, FontWeight.Bold),
    Font(R.font.gotham_black, FontWeight.Black),
    Font(R.font.gotham_medium, FontWeight.Medium),
    Font(R.font.gotham_book, FontWeight.Normal),
    Font(R.font.gotham_light, FontWeight.Light),
    Font(R.font.gotham_xlight, FontWeight.ExtraLight)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GothamFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = GothamFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GothamFont,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    labelSmall = TextStyle(
        fontFamily = GothamFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)