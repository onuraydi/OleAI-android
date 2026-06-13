package com.onur.oleai_android.features.auth.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.LoginRequest
import com.onur.oleai_android.core.network.dto.RegisterRequest
import retrofit2.HttpException
import com.onur.oleai_android.core.network.dto.AuthResponse
import com.onur.oleai_android.core.network.dto.LoginCommand
import com.onur.oleai_android.core.network.dto.RegisterCommand
import com.onur.oleai_android.core.network.dto.ForgotPasswordRequest
import com.onur.oleai_android.core.network.dto.ForgotPasswordCommand
import com.onur.oleai_android.core.network.dto.ResetPasswordRequest
import com.onur.oleai_android.core.network.dto.ResetPasswordCommand
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginCommand(request))
            Result.success(response)
        } catch (e: HttpException) {
            val message = when(e.code()) {
                400 -> "E-posta veya şifre hatalı"
                else -> e.message()
            }
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(RegisterCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<AuthResponse> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Result<AuthResponse> {
        return try {
            val response = apiService.resetPassword(ResetPasswordCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
