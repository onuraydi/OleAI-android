package com.onur.oleai_android.features.notifications.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.NotificationDto
import com.onur.oleai_android.core.network.dto.PaginatedResponse
import com.onur.oleai_android.core.network.dto.UnreadNotificationCountResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyNotifications(page: Int, size: Int, onlyUnread: Boolean): PaginatedResponse<NotificationDto> {
        return apiService.getMyNotifications(page = page, size = size, onlyUnread = onlyUnread)
    }

    suspend fun getUnreadNotificationCount(): UnreadNotificationCountResponse {
        return apiService.getUnreadNotificationCount()
    }

    suspend fun markNotificationAsRead(id: String) {
        apiService.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        apiService.markAllNotificationsAsRead()
    }

    suspend fun deleteNotification(id: String) {
        apiService.deleteNotification(id)
    }
}
