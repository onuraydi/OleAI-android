package com.onur.oleai_android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.R

import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val OutfitFontName = GoogleFont("Outfit")

val OutfitFontFamily = FontFamily(
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = OutfitFontName, fontProvider = provider, weight = FontWeight.ExtraBold)
)

val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = OutfitFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = OutfitFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = OutfitFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = OutfitFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = OutfitFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = OutfitFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = OutfitFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = OutfitFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = OutfitFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = OutfitFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = OutfitFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = OutfitFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = OutfitFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = OutfitFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = OutfitFontFamily)
)