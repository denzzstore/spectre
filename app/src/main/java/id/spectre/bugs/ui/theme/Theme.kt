package id.spectre.bugs.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Obsidian = Color(0xFF07070A)
val Onyx = Color(0xFF0E0E14)
val Gold = Color(0xFFD4AF37)
val GoldDim = Color(0xFF8A6F22)
val Ivory = Color(0xFFF2EDE4)
val Blood = Color(0xFFFF3B30)
val Glass = Color(0x14FFFFFF)

private val Scheme = darkColorScheme(
    primary = Gold,
    onPrimary = Obsidian,
    secondary = GoldDim,
    background = Obsidian,
    onBackground = Ivory,
    surface = Onyx,
    onSurface = Ivory,
    error = Blood
)

@Composable
fun SpectreTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = SpectreType, content = content)
}
