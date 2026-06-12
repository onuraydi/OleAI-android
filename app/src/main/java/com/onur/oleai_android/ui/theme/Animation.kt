package com.onur.oleai_android.ui.theme

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween

object AnimationConstants {
    const val DefaultDurationMillis = 200

    fun <T> defaultEaseOut() = tween<T>(
        durationMillis = DefaultDurationMillis,
        easing = LinearOutSlowInEasing
    )
}
