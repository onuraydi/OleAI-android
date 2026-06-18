package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyPantryItemResponse(
    val id: String,
    val masterIngredientId: String? = null,
    val name: String,
    val categoryName: String,
    val expirationDate: String? = null
)
