package com.example.cryptochallenge.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CryptoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColors,
        content = content
    )
}

private val LightColors = lightColors(
    primary = BlueLight,
    primaryVariant = BlueStrong,
    onPrimary = Color.White,
    secondary = BlueLight,
    secondaryVariant = BlueStrong,
    onSecondary = Color.White,
    error = Red800
)

@Composable
val Colors.onAppBarGradient: Color
    get() = if (isLight) Color.White
    else Color.Yellow
