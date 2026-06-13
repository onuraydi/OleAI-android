package com.onur.oleai_android.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.onur.oleai_android.features.auth.ui.ForgotPasswordScreen
import com.onur.oleai_android.features.auth.ui.LoginScreen
import com.onur.oleai_android.features.auth.ui.RegisterScreen
import com.onur.oleai_android.features.auth.ui.VerifyEmailScreen
import com.onur.oleai_android.features.auth.ui.ResetPasswordScreen
import com.onur.oleai_android.features.auth.ui.VerifyCodeScreen
import com.onur.oleai_android.features.auth.ui.SplashScreen
import com.onur.oleai_android.features.home.ui.HomeScreen
import com.onur.oleai_android.features.onboarding.ui.OnboardingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                onLoginSuccess = {
                    // Login başarılı olunca splash'e geri dönüp user profile kontrolü yapmasını sağlayalım.
                    // Ya da direkt checkInitialState yapabiliriz. splash route'una gitmek en temizi.
                    navController.navigate("splash") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRequireEmailVerification = { email ->
                    navController.navigate("verify_email/$email")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = { email ->
                    navController.navigate("verify_email/$email") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "verify_email/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyEmailScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onEmailVerified = {
                    navController.navigate("login") {
                        popUpTo("verify_email") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen()
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onCodeSent = { email ->
                    navController.navigate("verify_code/$email")
                }
            )
        }
        composable(
            route = "verify_code/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyCodeScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onCodeVerified = { otp ->
                    navController.navigate("reset_password/$email/$otp")
                }
            )
        }
        composable(
            route = "reset_password/{email}/{otp}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val otp = backStackEntry.arguments?.getString("otp") ?: ""
            ResetPasswordScreen(
                email = email,
                otp = otp,
                onNavigateBack = { navController.popBackStack() },
                onPasswordReset = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}
