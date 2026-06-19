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
import com.onur.oleai_android.core.network.dto.DietaryPreferenceListResponse
import com.onur.oleai_android.core.network.dto.AllergyRestrictionListResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse
import com.onur.oleai_android.core.network.dto.UpdateUserPreferencesRequest
import com.onur.oleai_android.core.network.dto.UpdateUserAllergiesRequest
import com.onur.oleai_android.core.network.dto.PopularDislikedIngredientResponse
import com.onur.oleai_android.core.network.dto.UpdateUserDislikedIngredientsRequest
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.core.network.dto.PaginatedResponse
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.TagDto
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsCommand
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsRequest
import com.onur.oleai_android.core.network.dto.AddedUserIngredientResponse
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import com.onur.oleai_android.core.network.dto.ShoppingListItemResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

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
        @Part firstName: MultipartBody.Part? = null,
        @Part lastName: MultipartBody.Part? = null,
        @Part username: MultipartBody.Part? = null,
        @Part birthDate: MultipartBody.Part? = null,
        @Part isOnboarded: MultipartBody.Part,
        @Part profileImage: MultipartBody.Part? = null
    ): UpdateProfileResponse

    @GET("/api/dietary-preferences")
    suspend fun getDietaryPreferences(): List<DietaryPreferenceListResponse>

    @GET("/api/allergy-restrictions")
    suspend fun getAllergyRestrictions(): List<AllergyRestrictionListResponse>

    @GET("/api/pantry/search")
    suspend fun searchIngredients(@Query("keyword") keyword: String): List<IngredientSearchResultResponse>

    @GET("/api/Pantry/popular-by-category")
    suspend fun getPopularIngredientsByCategory(): List<CategorizedPopularIngredientsResponse>

    @GET("/api/IngredientCategories")
    suspend fun getIngredientCategories(): List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse>

    @GET("/api/pantry/popular-dislikes")
    suspend fun getPopularDislikedIngredients(): List<PopularDislikedIngredientResponse>

    @PUT("/api/users/me/dietary-preferences")
    suspend fun updateDietaryPreferences(@Body request: UpdateUserPreferencesRequest): UpdateProfileResponse

    @PUT("/api/users/me/allergies")
    suspend fun updateAllergies(@Body request: UpdateUserAllergiesRequest): UpdateProfileResponse

    @PUT("/api/users/me/disliked-ingredients")
    suspend fun updateDislikedIngredients(@Body request: UpdateUserDislikedIngredientsRequest): UpdateProfileResponse

    @GET("/api/recipes")
    suspend fun getPublishedRecipes(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("searchTerm") searchTerm: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("maxTotalTime") maxTotalTime: Int? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("difficulty") difficulty: Int? = null,
        @Query("tagId") tagId: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("isForYou") isForYou: Boolean? = null
    ): PaginatedResponse<RecipeListItemResponse>

    @GET("/api/pantry")
    suspend fun getMyPantry(): List<MyPantryItemResponse>

    @POST("api/pantry/bulk")
    suspend fun addMultipleIngredients(@Body request: AddMultipleUserIngredientsCommand): List<AddedUserIngredientResponse>

    @PUT("/api/Pantry/{id}")
    suspend fun updateIngredientQuantity(
        @retrofit2.http.Path("id") id: String,
        @Body request: com.onur.oleai_android.core.network.dto.UpdateUserIngredientRequest
    ): com.onur.oleai_android.core.network.dto.GenericMessageResponse

    @retrofit2.http.DELETE("/api/Pantry/{id}")
    suspend fun deleteIngredient(
        @retrofit2.http.Path("id") id: String
    ): com.onur.oleai_android.core.network.dto.GenericMessageResponse

    @GET("/api/shopping-list")
    suspend fun getMyShoppingList(): List<ShoppingListItemResponse>

    @POST("/api/shopping-list")
    suspend fun addShoppingListItem(
        @Body request: com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest
    ): ShoppingListItemResponse

    @GET("/api/RecipeTaxonomy/categories")
    suspend fun getRecipeCategories(): List<RecipeCategoryResponse>

    @GET("/api/RecipeTaxonomy/tags")
    suspend fun getRecipeTags(): List<TagDto>

    @POST("/api/Auth/refresh-token")
    fun refreshToken(): Call<Unit>
}
