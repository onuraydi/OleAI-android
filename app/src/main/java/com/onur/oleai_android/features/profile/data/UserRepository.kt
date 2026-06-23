package com.onur.oleai_android.features.profile.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.UpdateProfileResponse
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import org.json.JSONObject
import com.onur.oleai_android.core.network.dto.UpdateUserPreferencesRequest
import com.onur.oleai_android.core.network.dto.UpdateUserAllergiesRequest
import com.onur.oleai_android.core.network.dto.UpdateUserDislikedIngredientsRequest
import com.onur.oleai_android.core.network.dto.ChangePasswordRequest
import com.onur.oleai_android.core.network.dto.AuthResponse
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

    suspend fun getUserStatistics(): Result<com.onur.oleai_android.core.network.dto.GetUserStatisticsResponse> {
        return try {
            val response = apiService.getUserStatistics()
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

    suspend fun updateUserProfile(
        firstName: String?,
        lastName: String?,
        username: String?,
        birthDate: String?,
        isOnboarded: Boolean,
        profileImagePart: MultipartBody.Part?
    ): Result<UpdateProfileResponse> {
        return try {
            val fnPart = firstName?.let { MultipartBody.Part.createFormData("FirstName", it) }
            val lnPart = lastName?.let { MultipartBody.Part.createFormData("LastName", it) }
            val unPart = username?.let { MultipartBody.Part.createFormData("Username", it) }
            val bdPart = birthDate?.let { MultipartBody.Part.createFormData("BirthDate", it) }
            val onboardedPart = MultipartBody.Part.createFormData("IsOnboarded", isOnboarded.toString())

            val response = apiService.updateProfile(
                firstName = fnPart,
                lastName = lnPart,
                username = unPart,
                birthDate = bdPart,
                isOnboarded = onboardedPart,
                profileImage = profileImagePart
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun updateDietaryPreferences(request: UpdateUserPreferencesRequest): Result<UpdateProfileResponse> {
        return try {
            val response = apiService.updateDietaryPreferences(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun updateAllergies(request: UpdateUserAllergiesRequest): Result<UpdateProfileResponse> {
        return try {
            val response = apiService.updateAllergies(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun updateDislikedIngredients(request: UpdateUserDislikedIngredientsRequest): Result<UpdateProfileResponse> {
        return try {
            val response = apiService.updateDislikedIngredients(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun changePassword(request: ChangePasswordRequest): Result<AuthResponse> {
        return try {
            val response = apiService.changePassword(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getMyRecipes(page: Int? = null, size: Int? = null, searchTerm: String? = null, categoryId: Int? = null, difficulty: Int? = null, maxTotalTime: Int? = null, tagId: Int? = null, minRating: Double? = null, visibility: String? = null, sortBy: String? = null): Result<com.onur.oleai_android.core.network.dto.PaginatedResponse<com.onur.oleai_android.core.network.dto.RecipeListItemResponse>> {
        return try {
            Result.success(apiService.getMyRecipes(page, size, searchTerm, categoryId, difficulty, maxTotalTime, tagId, minRating, visibility, sortBy))
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getSavedRecipes(page: Int? = null, size: Int? = null, searchTerm: String? = null, categoryId: Int? = null, difficulty: Int? = null, maxTotalTime: Int? = null, tagId: Int? = null, minRating: Double? = null, visibility: String? = null, sortBy: String? = null): Result<com.onur.oleai_android.core.network.dto.PaginatedResponse<com.onur.oleai_android.core.network.dto.RecipeListItemResponse>> {
        return try {
            Result.success(apiService.getSavedRecipes(page, size, searchTerm, categoryId, difficulty, maxTotalTime, tagId, minRating, visibility, sortBy))
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getLikedRecipes(page: Int? = null, size: Int? = null, searchTerm: String? = null, categoryId: Int? = null, difficulty: Int? = null, maxTotalTime: Int? = null, tagId: Int? = null, minRating: Double? = null, visibility: String? = null, sortBy: String? = null): Result<com.onur.oleai_android.core.network.dto.PaginatedResponse<com.onur.oleai_android.core.network.dto.RecipeListItemResponse>> {
        return try {
            Result.success(apiService.getLikedRecipes(page, size, searchTerm, categoryId, difficulty, maxTotalTime, tagId, minRating, visibility, sortBy))
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getDislikedRecipes(page: Int? = null, size: Int? = null, searchTerm: String? = null, categoryId: Int? = null, difficulty: Int? = null, maxTotalTime: Int? = null, tagId: Int? = null, minRating: Double? = null, visibility: String? = null, sortBy: String? = null): Result<com.onur.oleai_android.core.network.dto.PaginatedResponse<com.onur.oleai_android.core.network.dto.RecipeListItemResponse>> {
        return try {
            Result.success(apiService.getDislikedRecipes(page, size, searchTerm, categoryId, difficulty, maxTotalTime, tagId, minRating, visibility, sortBy))
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getRecipeTags(): Result<List<com.onur.oleai_android.core.network.dto.TagDto>> {
        return try {
            Result.success(apiService.getRecipeTags())
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun getMyComments(page: Int? = null, size: Int? = null, searchTerm: String? = null, sortBy: String? = null): Result<com.onur.oleai_android.core.network.dto.PaginatedResponse<com.onur.oleai_android.core.network.dto.MyCommentDto>> {
        return try {
            Result.success(apiService.getMyComments(page, size, searchTerm, sortBy))
        } catch (e: Exception) {
            Result.failure(handleException(e))
        }
    }

    suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            val response = apiService.deleteComment(commentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Yorum silinemedi"))
            }
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
