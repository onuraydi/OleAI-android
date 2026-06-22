package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyShoppingListItemResponse(
    val id: String,
    val ingredientId: String? = null,
    val ingredientRequestId: String? = null,
    val ingredientName: String,
    val categoryName: String,
    val categoryIconName: String,
    val quantity: Double,
    val unit: String = "",
    val isPurchased: Boolean,
    val isPendingApproval: Boolean = false
)

@Serializable
data class ShoppingListItemResponse(
    val id: String,
    val isPurchased: Boolean
)

@Serializable
data class AddShoppingListItemRequest(
    val ingredientId: String? = null,
    val requestedName: String? = null,
    val suggestedCategoryId: String? = null,
    val quantity: Double,
    val unit: Int
)

@Serializable
data class AddMultipleShoppingListItemsRequest(
    val items: List<AddShoppingListItemRequest>
)

@Serializable
data class UpdateShoppingListItemRequest(
    val newQuantity: Double,
    val newUnit: Int
)

