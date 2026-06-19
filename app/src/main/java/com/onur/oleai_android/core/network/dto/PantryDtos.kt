package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyPantryItemResponse(
    val id: String,
    val masterIngredientId: String? = null,
    val name: String,
    val categoryName: String,
    val categoryIconName: String = "",
    val quantity: Double,
    val unit: String = "",
    val expirationDate: String? = null,
    val isPendingApproval: Boolean = false
)

@Serializable
data class IngredientCategoryResponse(
    val id: String,
    val name: String,
    val iconName: String
)

@Serializable
data class AddUserIngredientRequest(
    val userId: String,
    val ingredientId: String? = null,
    val requestedName: String? = null,
    val suggestedCategoryId: String? = null,
    val quantity: Double,
    val unit: Int, // MeasurementUnit enum represented as Int
    val expirationDate: String? = null,
    val estimatedShelfLifeInDays: Int? = null
)

@Serializable
data class AddMultipleUserIngredientsRequest(
    val ingredients: List<AddUserIngredientRequest>
)

@Serializable
data class AddMultipleUserIngredientsCommand(
    val request: AddMultipleUserIngredientsRequest
)

@Serializable
data class AddedUserIngredientResponse(
    val id: String,
    val masterIngredientId: String? = null,
    val name: String? = null,
    val isPendingApproval: Boolean = false
)

@Serializable
data class UpdateUserIngredientRequest(
    val newQuantity: Double,
    val newUnit: Int? = null,
    val expirationDate: String? = null
)

@Serializable
data class GenericMessageResponse(
    val message: String
)
