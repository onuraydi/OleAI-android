package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemResponse(
    val id: String,
    val isPurchased: Boolean
)
