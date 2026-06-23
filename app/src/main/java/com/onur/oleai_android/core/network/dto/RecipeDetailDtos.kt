package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailResponse(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val visibility: String,
    val status: String,
    val difficulty: String,
    val servings: Int,
    val prepTime: Int,
    val cookTime: Int,
    val createdDate: String,
    val updatedDate: String? = null,
    val ingredients: List<RecipeIngredientResponse> = emptyList(),
    val steps: List<RecipeStepResponse> = emptyList(),
    val images: List<RecipeImageResponse> = emptyList(),
    val categories: List<RecipeCategoryResponse> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val statistics: RecipeStatisticsResponse = RecipeStatisticsResponse(),
    val userInteraction: UserInteractionResponse? = null,
    val author: BasicUserInfo? = null
)

@Serializable
data class RecipeIngredientResponse(
    val id: String? = null,
    val recipeId: String? = null,
    val ingredientId: String? = null,
    val ingredientName: String? = null,
    val quantity: Double? = null,
    val amount: Double? = null,
    val unit: String? = null,
    val unitId: Int? = null,
    val unitFamily: Int? = null,
    val ingredient: IngredientResponse? = null
)

@Serializable
data class IngredientResponse(
    val id: String,
    val name: String,
    val iconName: String? = null,
    val unitId: Int? = null,
    val isStaple: Boolean = false
)

@Serializable
data class RecipeStepResponse(
    val id: String? = null,
    val recipeId: String? = null,
    val stepNumber: Int = 0,
    val instruction: String = "",
    val imageUrl: String? = null
)

@Serializable
data class RecipeImageResponse(
    val id: String? = null,
    val recipeId: String? = null,
    val imageUrl: String = "",
    val isMainImage: Boolean? = null,
    val isMain: Boolean? = null,
    val stepId: String? = null
)

@Serializable
data class RecipeStatisticsResponse(
    val totalComments: Int = 0,
    val totalLikes: Int = 0,
    val totalDislikes: Int = 0,
    val totalSaves: Int = 0,
    val averageRating: Double = 0.0,
    val totalRatings: Int = 0
)

@Serializable
data class UserInteractionResponse(
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false
)

@Serializable
data class BasicUserInfo(
    val userId: String,
    val username: String,
    val profileImageUrl: String? = null
)

@Serializable
data class RecipeCommentResponse(
    val id: String,
    val userId: String,
    val username: String,
    val profileImageUrl: String? = null,
    val content: String,
    val parentCommentId: String? = null,
    val rating: Int? = null,
    val createdDate: String,
    val replies: List<RecipeCommentResponse> = emptyList()
)

@Serializable
data class StartCookingRequest(
    val ignoredIngredientIds: List<String> = emptyList()
)

@Serializable
data class ToggleInteractionResponse(
    val message: String
)

@Serializable
data class AddCommentRequest(
    val content: String,
    val rating: Int? = null,
    val parentCommentId: String? = null
)

@Serializable
data class UpdateCommentRequest(
    val newContent: String,
    val rating: Int? = null
)
