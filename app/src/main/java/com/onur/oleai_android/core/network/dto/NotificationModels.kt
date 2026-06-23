package com.onur.oleai_android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val relatedEntityId: String? = null,
    val isRead: Boolean,
    val createdDate: String
)

@Serializable
data class UnreadNotificationCountResponse(
    val count: Int
)
