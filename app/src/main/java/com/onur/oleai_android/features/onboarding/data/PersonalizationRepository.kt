package com.onur.oleai_android.features.onboarding.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MultipartBody
import javax.inject.Inject

class PersonalizationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getDietaryPreferences(): Result<List<DietaryPreferenceListResponse>> {
        return try {
            val response = apiService.getDietaryPreferences()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllergyRestrictions(): Result<List<AllergyRestrictionListResponse>> {
        return try {
            val response = apiService.getAllergyRestrictions()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchIngredients(keyword: String): Result<List<IngredientSearchResultResponse>> {
        return try {
            val response = apiService.searchIngredients(keyword)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularIngredientsByCategory(): Result<List<CategorizedPopularIngredientsResponse>> {
        return try {
            val response = apiService.getPopularIngredientsByCategory()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularDislikedIngredients(): Result<List<PopularDislikedIngredientResponse>> {
        return try {
            val response = apiService.getPopularDislikedIngredients()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun savePersonalization(
        username: String,
        birthDate: String?, // yyyy-MM-dd format expected by backend
        profileImageBytes: ByteArray?,
        dietaryPreferenceIds: List<String>,
        allergyRestrictionIds: List<String>,
        dislikedIngredientIds: List<String>
    ): Result<Unit> {
        return try {
            // 1. Update Profile (Username, BirthDate, IsOnboarded = true)
            val usernamePart = MultipartBody.Part.createFormData("Username", username)
            val isOnboardedPart = MultipartBody.Part.createFormData("IsOnboarded", "true")
            val birthDatePart = birthDate?.let { MultipartBody.Part.createFormData("BirthDate", it) }
            
            val profileImagePart = profileImageBytes?.let { bytes ->
                val reqFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("ProfileImage", "profile.jpg", reqFile)
            }
            
            apiService.updateProfile(
                username = usernamePart,
                birthDate = birthDatePart,
                isOnboarded = isOnboardedPart,
                profileImage = profileImagePart
            )

            // 2. Update Dietary Preferences
            if (dietaryPreferenceIds.isNotEmpty()) {
                apiService.updateDietaryPreferences(UpdateUserPreferencesRequest(dietaryPreferenceIds))
            }

            // 3. Update Allergies
            if (allergyRestrictionIds.isNotEmpty()) {
                apiService.updateAllergies(UpdateUserAllergiesRequest(allergyRestrictionIds))
            }

            // 4. Update Disliked Ingredients
            if (dislikedIngredientIds.isNotEmpty()) {
                apiService.updateDislikedIngredients(UpdateUserDislikedIngredientsRequest(dislikedIngredientIds))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
