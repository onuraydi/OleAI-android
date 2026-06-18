package com.onur.oleai_android.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.features.auth.ui.ForgotPasswordScreen
import com.onur.oleai_android.features.auth.ui.LoginScreen
import com.onur.oleai_android.features.auth.ui.RegisterScreen
import com.onur.oleai_android.features.auth.ui.VerifyEmailScreen
import com.onur.oleai_android.features.auth.ui.ResetPasswordScreen
import com.onur.oleai_android.features.auth.ui.VerifyCodeScreen
import com.onur.oleai_android.features.auth.ui.SplashScreen
import com.onur.oleai_android.features.home.ui.HomeScreen
import com.onur.oleai_android.features.onboarding.ui.OnboardingScreen
import com.onur.oleai_android.features.onboarding.ui.ProfileSetupScreen
import com.onur.oleai_android.features.onboarding.ui.DietaryPreferencesScreen
import com.onur.oleai_android.features.onboarding.ui.AllergiesScreen
import com.onur.oleai_android.features.onboarding.ui.DislikedIngredientsScreen
import com.onur.oleai_android.features.onboarding.viewmodel.PersonalizationViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object VerifyEmail : Screen("verify_email/{email}")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object PersonalizationProfile : Screen("personalization_profile")
    object PersonalizationDiet : Screen("personalization_diet")
    object PersonalizationAllergies : Screen("personalization_allergies")
    object PersonalizationDisliked : Screen("personalization_disliked")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToPersonalization = {
                    navController.navigate(Screen.PersonalizationProfile.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToPersonalization = {
                    navController.navigate(Screen.PersonalizationProfile.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRequireEmailVerification = { email ->
                    navController.navigate("verify_email/$email")
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = { email ->
                    navController.navigate("verify_email/$email") {
                        popUpTo(Screen.Register.route) { inclusive = true }
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
                    navController.navigate(Screen.Login.route) {
                        popUpTo("verify_email") { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.ForgotPassword.route) {
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

        // --- Personalization Flow ---
        composable(Screen.PersonalizationProfile.route) { backStackEntry ->
            val viewModel: PersonalizationViewModel = hiltViewModel(backStackEntry)
            ProfileSetupScreen(
                viewModel = viewModel,
                onNavigateNext = {
                    navController.navigate(Screen.PersonalizationDiet.route)
                }
            )
        }

        composable(Screen.PersonalizationDiet.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.PersonalizationProfile.route)
            }
            val viewModel: PersonalizationViewModel = hiltViewModel(parentEntry)
            DietaryPreferencesScreen(
                viewModel = viewModel,
                onNavigateNext = {
                    navController.navigate(Screen.PersonalizationAllergies.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PersonalizationAllergies.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.PersonalizationProfile.route)
            }
            val viewModel: PersonalizationViewModel = hiltViewModel(parentEntry)
            AllergiesScreen(
                viewModel = viewModel,
                onNavigateNext = {
                    navController.navigate(Screen.PersonalizationDisliked.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PersonalizationDisliked.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.PersonalizationProfile.route)
            }
            val viewModel: PersonalizationViewModel = hiltViewModel(parentEntry)
            val userViewModel: com.onur.oleai_android.features.profile.viewmodel.UserViewModel = hiltViewModel()
            DislikedIngredientsScreen(
                viewModel = viewModel,
                onComplete = {
                    userViewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PersonalizationProfile.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
