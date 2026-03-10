package com.abdullahnadeem.minecraftandroid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val LightColors = lightColorScheme(
    primary = MinecraftGrass,
    onPrimary = MinecraftSand,
    secondary = MinecraftStone,
    onSecondary = MinecraftSand,
    background = MinecraftSand,
    onBackground = MinecraftStone,
    surface = MinecraftSky,
    onSurface = MinecraftStone,
    error = MinecraftError
)

private val DarkColors = darkColorScheme(
    primary = MinecraftGrass,
    onPrimary = MinecraftStone,
    secondary = MinecraftSky,
    onSecondary = MinecraftStone,
    background = MinecraftStone,
    onBackground = MinecraftSand,
    surface = ColorTokens.SurfaceDark,
    onSurface = MinecraftSand,
    error = MinecraftError
)

private object ColorTokens {
    val SurfaceDark = MinecraftStone.copy(alpha = 0.92f)
}

@Composable
fun MinecraftAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}