package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val profileImageUrl: String? = null,
    val isOnboarded: Boolean,
    val isEmailVerified: Boolean
)

@Serializable
data class UpdateProfileResponse(
    val message: String
)
