package com.eddy.nrf.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.eddy.nrf.R

val sfPro = FontFamily(
    Font(R.font.sf_pro_semibold, FontWeight.Normal),
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = sfPro,
        fontWeight = FontWeight.Normal,
        fontSize = 70.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = sfPro,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = sfPro,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
)