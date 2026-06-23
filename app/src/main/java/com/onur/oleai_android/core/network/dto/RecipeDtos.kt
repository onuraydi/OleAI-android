package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RecipeListItemResponse(
    val id: String,
    val title: String,
    val description: String? = null,
    val mainImageUrl: String? = null,
    val averageRating: Double? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val difficulty: Int? = null,
    val tags: List<TagDto>? = null
)

@Serializable
data class TagDto(
    val id: Int,
    val name: String
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T> = emptyList(),
    val pageNumber: Int = 0,
    val pageSize: Int = 10,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val hasPreviousPage: Boolean = false,
    val hasNextPage: Boolean = false,
    val index: Int = 0,
    val size: Int = 10,
    val count: Int = 0,
    val pages: Int = 0,
    val hasPrevious: Boolean = false,
    val hasNext: Boolean = false
)

@Serializable
data class RecipeCategoryResponse(
    val id: Int,
    val name: String,
    val iconName: String
)

@Serializable
data class CreateRecipeIngredientRequest(
    @SerialName("IngredientId") val ingredientId: String? = null,
    @SerialName("RequestedName") val requestedName: String? = null,
    @SerialName("SuggestedCategoryId") val suggestedCategoryId: String? = null,
    @SerialName("Amount") val amount: Double,
    @SerialName("UnitId") val unitId: Int
)

@Serializable
data class CreateRecipeStepRequest(
    @SerialName("StepNumber") val stepNumber: Int,
    @SerialName("Instruction") val instruction: String
)
