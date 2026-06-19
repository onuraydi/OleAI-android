package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

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
