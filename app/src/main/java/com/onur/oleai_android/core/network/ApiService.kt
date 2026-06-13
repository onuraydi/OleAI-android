package com.onur.oleai_android.core.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.onur.oleai_android.core.network.dto.LoginCommand
import com.onur.oleai_android.core.network.dto.RegisterCommand
import com.onur.oleai_android.core.network.dto.ForgotPasswordCommand
import com.onur.oleai_android.core.network.dto.ResetPasswordCommand
import com.onur.oleai_android.core.network.dto.VerifyEmailCommand
import com.onur.oleai_android.core.network.dto.ResendOtpCommand
import com.onur.oleai_android.core.network.dto.AuthResponse
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.core.network.dto.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @POST("/api/Auth/login")
    suspend fun login(@Body command: LoginCommand): AuthResponse

    @POST("/api/Auth/register")
    suspend fun register(@Body command: RegisterCommand): AuthResponse

    @POST("/api/Auth/forgot-password")
    suspend fun forgotPassword(@Body command: ForgotPasswordCommand): AuthResponse

    @POST("/api/Auth/reset-password")
    suspend fun resetPassword(@Body command: ResetPasswordCommand): AuthResponse

    @POST("/api/Auth/verify-email")
    suspend fun verifyEmail(@Body command: VerifyEmailCommand): AuthResponse

    @POST("/api/Auth/resend-otp")
    suspend fun resendOtp(@Body command: ResendOtpCommand): AuthResponse

    @GET("/api/users/me")
    suspend fun getUserProfile(): UserProfileResponse

    @Multipart
    @PUT("/api/users/me")
    suspend fun updateProfile(
        @Part("FirstName") firstName: RequestBody? = null,
        @Part("LastName") lastName: RequestBody? = null,
        @Part("Username") username: RequestBody? = null,
        @Part("IsOnboarded") isOnboarded: RequestBody,
        @Part profileImage: MultipartBody.Part? = null
    ): UpdateProfileResponse

    @POST("/api/Auth/refresh-token")
    fun refreshToken(): Call<Unit>
}
