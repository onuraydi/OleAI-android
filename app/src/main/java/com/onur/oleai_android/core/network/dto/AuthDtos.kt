package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val message: String? = null
)

@Serializable
data class LoginCommand(
    val request: LoginRequest
)

@Serializable
data class RegisterCommand(
    val request: RegisterRequest
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordCommand(
    val request: ForgotPasswordRequest
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val otpCode: String,
    val newPassword: String
)

@Serializable
data class ResetPasswordCommand(
    val request: ResetPasswordRequest
)
