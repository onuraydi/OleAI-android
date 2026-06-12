package com.onur.oleai_android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.R

val OutfitFontFamily = FontFamily(
    Font(R.font.outfit, FontWeight.Normal)
)

val OutfitFontFeatureSettings = "\"rlig\" 1, \"calt\" 1"

val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = OutfitFontFamily, fontFeatureSettings = OutfitFontFeatureSettings)
)