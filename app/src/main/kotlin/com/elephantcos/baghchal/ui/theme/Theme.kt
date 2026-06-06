package com.elephantcos.baghchal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Colors = darkColorScheme(
    primary       = TigerOrange,
    onPrimary     = TextDark,
    secondary     = LightWood,
    onSecondary   = TextDark,
    background    = DeepGreen,
    onBackground  = TextLight,
    surface       = WoodBrown,
    onSurface     = TextLight
)

@Composable
fun BaghChalTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Typography, content = content)
}
