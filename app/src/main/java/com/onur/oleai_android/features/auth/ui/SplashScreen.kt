package com.onur.oleai_android.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.features.profile.viewmodel.UserViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val authState by userViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.checkInitialState()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is UserViewModel.AuthState.Unauthenticated -> onNavigateToLogin()
            is UserViewModel.AuthState.OnboardingRequired -> onNavigateToOnboarding()
            is UserViewModel.AuthState.Authenticated -> onNavigateToHome()
            else -> {} // Loading state
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF8DBE55))
    }
}
