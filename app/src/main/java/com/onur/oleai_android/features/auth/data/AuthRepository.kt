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
import com.onur.oleai_android.core.network.dto.VerifyEmailRequest
import com.onur.oleai_android.core.network.dto.VerifyEmailCommand
import com.onur.oleai_android.core.network.dto.ResendOtpCodeRequest
import com.onur.oleai_android.core.network.dto.ResendOtpCommand
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

import com.onur.oleai_android.core.network.cookie.AppCookieJar

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val cookieJar: AppCookieJar
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (e: Exception) {
            // Log or ignore, we still want to clear local cookies
        } finally {
            cookieJar.clearCookies()
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(RegisterCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): Result<AuthResponse> {
        return try {
            val response = apiService.forgotPassword(ForgotPasswordCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): Result<AuthResponse> {
        return try {
            val response = apiService.resetPassword(ResetPasswordCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun verifyEmail(request: VerifyEmailRequest): Result<AuthResponse> {
        return try {
            val response = apiService.verifyEmail(VerifyEmailCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun resendOtp(request: ResendOtpCodeRequest): Result<AuthResponse> {
        return try {
            val response = apiService.resendOtp(ResendOtpCommand(request))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    private fun handleException(e: Exception): Exception {
        if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            var message = "Bir hata oluştu"
            try {
                if (!errorBody.isNullOrBlank()) {
                    val jsonObject = JSONObject(errorBody)
                    if (jsonObject.has("detail")) {
                        message = jsonObject.getString("detail")
                    } else if (jsonObject.has("message")) {
                        message = jsonObject.getString("message")
                    }
                } else if (e.code() == 400) {
                    message = "Geçersiz istek (400)"
                }
                return Exception(message)
            } catch (jsonEx: Exception) {
                return Exception(if (e.code() == 400) "Geçersiz istek" else e.message())
            }
        }
        return e
    }
}


