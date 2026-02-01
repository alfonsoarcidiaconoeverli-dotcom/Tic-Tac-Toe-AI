package com.org.tictactoe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Premium palette (coerente col reskin)
private val PremiumDarkScheme = darkColorScheme(
    primary = Color(0xFF0F172A),      // brand_primary
    secondary = Color(0xFF38BDF8),    // brand_secondary (X)
    tertiary = Color(0xFF22C55E),     // brand_accent (O)

    background = Color(0xFF020617),
    surface = Color(0xFF020617),
    onPrimary = Color(0xFFF8FAFC),
    onSecondary = Color(0xFF020617),
    onTertiary = Color(0xFF020617),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
)

private val PremiumLightScheme = lightColorScheme(
    primary = Color(0xFF0F172A),
    secondary = Color(0xFF0284C7),
    tertiary = Color(0xFF16A34A),

    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun TicTacToeTheme(
    darkTheme: Boolean = true, // ✅ Premium: di default dark (puoi cambiarlo dopo con settings)
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme || isSystemInDarkTheme()) {
        PremiumDarkScheme
    } else {
        PremiumLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
