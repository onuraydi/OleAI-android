package com.onur.oleai_android.features.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onur.oleai_android.core.network.dto.NotificationDto
import com.onur.oleai_android.features.notifications.data.NotificationRepository
import com.onur.oleai_android.features.notifications.data.NotificationsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val notificationsPagingData: Flow<PagingData<NotificationDto>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { NotificationsPagingSource(repository, onlyUnread = false) }
    ).flow.cachedIn(viewModelScope)

    fun markAsRead(id: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(id)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun markAllAsRead(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.markAllNotificationsAsRead()
                onSuccess()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(id)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}
