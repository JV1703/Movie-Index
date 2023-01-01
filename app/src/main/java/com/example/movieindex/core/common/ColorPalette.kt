package com.example.movieindex.core.common

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette

class ColorPalette {

    fun calcDominantColor(drawable: Drawable, onFinish: (Int) -> Unit) {
        val bitmap = transformToBitMap(drawable)
        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(colorValue)
            }
        }
    }

    fun calcDominantColor(drawable: Drawable): Int? {
        var dominantColor: Int? = null
        val bitmap = transformToBitMap(drawable)
        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                dominantColor = colorValue
            }
        }
        return dominantColor
    }

    private fun transformToBitMap(drawable: Drawable): Bitmap {
        return (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.RGB_565, true)
    }

}