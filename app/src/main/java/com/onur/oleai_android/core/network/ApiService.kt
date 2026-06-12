package com.onur.oleai_android.core.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.onur.oleai_android.core.network.dto.LoginCommand
import com.onur.oleai_android.core.network.dto.RegisterCommand
import com.onur.oleai_android.core.network.dto.AuthResponse

interface ApiService {
    @POST("/api/Auth/login")
    suspend fun login(@Body command: LoginCommand): AuthResponse

    @POST("/api/Auth/register")
    suspend fun register(@Body command: RegisterCommand): AuthResponse

    @POST("/api/Auth/refresh-token")
    fun refreshToken(): Call<Unit>
}
