package com.onur.oleai_android.features.profile.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.UpdateProfileResponse
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserProfile(): Result<UserProfileResponse> {
        return try {
            val response = apiService.getUserProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun setOnboardedStatus(isOnboarded: Boolean): Result<UpdateProfileResponse> {
        return try {
            // Convert boolean to RequestBody for multipart/form-data
            val isOnboardedPart = MultipartBody.Part.createFormData("IsOnboarded", isOnboarded.toString())
            
            val response = apiService.updateProfile(
                isOnboarded = isOnboardedPart
            )
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
                } else if (e.code() == 401) {
                    message = "Oturum süresi doldu"
                }
                return Exception(message)
            } catch (jsonEx: Exception) {
                return Exception(if (e.code() == 400) "Geçersiz istek" else e.message())
            }
        }
        return e
    }
}
