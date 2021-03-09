package com.example.cryptochallenge.ui.components

import android.graphics.drawable.GradientDrawable
import android.view.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Composable
fun SetStatusBarGradient(
    window: Window?,
    startColor: Int = MaterialTheme.colors.primary.toArgb(),
    endColor: Int = MaterialTheme.colors.primaryVariant.toArgb()
) {
    val colors = IntArray(2) {
        if (it == 0) startColor
        else endColor
    }
    val myGradient = GradientDrawable()
    myGradient.orientation = GradientDrawable.Orientation.LEFT_RIGHT
    myGradient.colors = colors

    window?.setBackgroundDrawable(myGradient)
}

@Composable
fun horizontalGradient(color: Color): Brush =
    Brush.horizontalGradient(
        listOf(
            color.copy(0.8f),
            color
        )
    )