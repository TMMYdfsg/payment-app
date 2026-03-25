package com.payment.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.payment.app.R

private val manropeFamily = FontFamily(
    Font(resId = R.font.manrope_variable, weight = FontWeight.Medium),
    Font(resId = R.font.manrope_variable, weight = FontWeight.SemiBold),
    Font(resId = R.font.manrope_variable, weight = FontWeight.Bold),
    Font(resId = R.font.manrope_variable, weight = FontWeight.ExtraBold)
)

private val interFamily = FontFamily(
    Font(resId = R.font.inter_variable, weight = FontWeight.Normal),
    Font(resId = R.font.inter_variable, weight = FontWeight.Medium),
    Font(resId = R.font.inter_variable, weight = FontWeight.SemiBold),
    Font(resId = R.font.inter_variable, weight = FontWeight.Bold)
)

private val headlineFamily = manropeFamily
private val bodyFamily = interFamily

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = headlineFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.4).sp
    ),
    displayMedium = TextStyle(
        fontFamily = headlineFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = headlineFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = headlineFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = headlineFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.6.sp
    ),
    labelSmall = TextStyle(
        fontFamily = bodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.6.sp
    )
)
