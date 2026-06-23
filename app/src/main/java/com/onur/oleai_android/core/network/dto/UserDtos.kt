package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietaryPreferenceItemResponse(
    val id: String,
    val name: String
)

@Serializable
data class AllergyRestrictionItemResponse(
    val id: String,
    val name: String
)

@Serializable
data class DislikedIngredientItemResponse(
    val id: String,
    val name: String
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val birthDate: String? = null,
    val profileImageUrl: String? = null,
    val isOnboarded: Boolean,
    val isEmailVerified: Boolean,
    val createdDate: String,
    val dietaryPreferences: List<DietaryPreferenceItemResponse> = emptyList(),
    val allergies: List<AllergyRestrictionItemResponse> = emptyList(),
    val dislikedIngredients: List<DislikedIngredientItemResponse> = emptyList()
)

@Serializable
data class GetUserStatisticsResponse(
    val totalRecipesCreated: Int,
    val totalRecipesSaved: Int,
    val totalPantryItems: Int,
    val totalRecipesLiked: Int
)

@Serializable
data class UpdateProfileResponse(
    val message: String
)

@Serializable
data class MyCommentDto(
    val id: String? = null,
    val recipeId: String? = null,
    val recipeName: String? = null,
    val recipeImageUrl: String? = null,
    val content: String? = null,
    val rating: Int? = null,
    val createdDate: String? = null
) {
    val actualId: String get() = id ?: ""
}
