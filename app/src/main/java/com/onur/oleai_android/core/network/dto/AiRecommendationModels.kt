package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiRecommendationRequest(
    val analysisMode: String, // "PantryOnly", "Standard", etc.
    val preventWaste: Boolean,
    val allergyProtection: Boolean,
    val strictDietRules: Boolean,
    val excludeDisliked: Boolean
)

@Serializable
data class AiRecommendationResponse(
    val matchScore: Double = 0.0,
    val isFallback: Boolean = false,
    val recipe: RecipeListItemResponse
)

@Serializable
data class AiRecommendationResult(
    val results: List<AiRecommendationResponse>,
    val totalCount: Int,
    val hasPerfectMatch: Boolean
)
