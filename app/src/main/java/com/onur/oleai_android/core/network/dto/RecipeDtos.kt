package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

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
    val items: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean
)

@Serializable
data class RecipeCategoryResponse(
    val id: Int,
    val name: String,
    val iconName: String
)
