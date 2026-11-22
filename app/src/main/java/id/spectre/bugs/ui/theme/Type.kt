package id.spectre.bugs.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.spectre.bugs.R

val Sora = FontFamily(Font(R.font.sora_variable, FontWeight.Normal))
val Playfair = FontFamily(Font(R.font.playfair_variable, FontWeight.Bold))

val SpectreType = Typography(
    displayLarge = TextStyle(fontFamily = Playfair, fontSize = 42.sp, letterSpacing = 1.sp),
    titleLarge = TextStyle(fontFamily = Playfair, fontSize = 23.sp, letterSpacing = 0.5.sp),
    titleMedium = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = Sora, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = Sora, fontSize = 11.sp, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = Sora, fontSize = 10.sp, letterSpacing = 1.4.sp)
)
