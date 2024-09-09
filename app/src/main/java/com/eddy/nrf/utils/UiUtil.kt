package com.eddy.nrf.utils

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap

object UiUtil {
    @Composable
    fun XmlDrawableToBitmap(
        @DrawableRes resourceId: Int
    ): ImageBitmap? =
        ContextCompat.getDrawable(
            LocalContext.current,
            resourceId
        )?.toBitmap()?.asImageBitmap()
}