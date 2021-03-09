package com.example.cryptochallenge.utils

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable

@Composable
fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colors.surface,
    defaultOnColor: Color = MaterialTheme.colors.onSurface,
    cache: LruCache<String, DominantColors> = LruCache<String, DominantColors>(10)
): DominantColorState = remember {
    DominantColorState(context, defaultColor, defaultOnColor, cache)
}

class DominantColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    private val cache: LruCache<String, DominantColors>
) {

    var color by mutableStateOf(defaultColor)
        private set
    var onColor by mutableStateOf(defaultOnColor)
        private set

    suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDominantColor(url)
        color = result?.color ?: defaultColor
        onColor = result?.onColor ?: defaultOnColor
    }

    private suspend fun calculateDominantColor(url: String): DominantColors? {
        val cached = cache.get(url)
        if (cached != null) return cached

        return calculateSwatchesInImage(context, url)
            .sortedByDescending { swatch -> swatch.population }
            .firstOrNull()
            ?.let { swatch ->
                DominantColors(
                    color = Color(swatch.rgb),
                    onColor = Color(swatch.bodyTextColor)
                )
            }
            ?.also { colors -> cache.put(url, colors) }
    }

    private suspend fun calculateSwatchesInImage(
        context: Context,
        imageUrl: String
    ): List<Palette.Swatch> {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(50)
            .allowHardware(false)
            .build()

        val bitmap = when (val result = Coil.execute(request)) {
            is SuccessResult -> result.drawable.toBitmap()
            else -> null
        }

        return bitmap?.let {
            withContext(Dispatchers.Default) {
                val palette = Palette.from(bitmap)
                    .resizeBitmapArea(0)
                    .maximumColorCount(4)
                    .generate()
                palette.swatches
            }
        } ?: emptyList()
    }
}
data class DominantColors(val color: Color, val onColor: Color) : Serializable