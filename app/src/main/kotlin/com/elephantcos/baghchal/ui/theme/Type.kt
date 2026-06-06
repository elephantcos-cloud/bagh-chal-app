package com.elephantcos.baghchal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleLarge  = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    bodyLarge   = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 16.sp),
    labelLarge  = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 14.sp)
)
