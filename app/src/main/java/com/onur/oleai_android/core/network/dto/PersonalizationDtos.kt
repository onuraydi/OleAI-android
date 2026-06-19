package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class DietaryPreferenceListResponse(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val iconName: String = ""
)

@Serializable
data class AllergyRestrictionListResponse(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val iconName: String = ""
)

@Serializable
data class IngredientSearchResultResponse(
    val masterIngredientId: String = "",
    val displayName: String = "",
    val isAlias: Boolean = false,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val categoryIconName: String? = null,
    val unitFamily: String = "Count" // Default Count
)

@Serializable
data class PopularDislikedIngredientResponse(
    val id: String = "",
    val name: String = "",
    val categoryName: String = "",
    val categoryIconName: String? = null
)

@Serializable
data class CategorizedPopularIngredientsResponse(
    val categoryId: String? = null,
    val categoryName: String? = null,
    val categoryIconName: String? = null,
    val ingredients: List<IngredientSearchResultResponse> = emptyList()
)

@Serializable
data class UpdateUserPreferencesRequest(
    val dietaryPreferenceIds: List<String>
)

@Serializable
data class UpdateUserAllergiesRequest(
    val allergyRestrictionIds: List<String>
)

@Serializable
data class UpdateUserDislikedIngredientsRequest(
    val ingredientIds: List<String>
)
