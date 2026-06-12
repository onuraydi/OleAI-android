package com.onur.oleai_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.onur.oleai_android.core.ui.navigation.AppNavigation
import com.onur.oleai_android.ui.theme.OleAIandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OleAIandroidTheme {
                AppNavigation()
            }
        }
    }
}