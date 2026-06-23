package com.onur.oleai_android.features.profile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class EditProfileState(
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val birthDate: String = "",
    val birthDateError: String? = null,
    val initialProfileImageUrl: String? = null,
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    private var isOnboarded: Boolean = true

    fun initData(userProfile: UserProfileResponse) {
        if (_state.value.firstName.isEmpty() && _state.value.lastName.isEmpty()) {
            var displayDate = userProfile.birthDate ?: ""
            if (displayDate.isNotBlank()) {
                try {
                    val parts = displayDate.split("T")[0].split("-")
                    if (parts.size == 3) {
                        displayDate = "${parts[2]}${parts[1]}${parts[0]}"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            _state.value = _state.value.copy(
                firstName = userProfile.firstName,
                lastName = userProfile.lastName,
                username = userProfile.username,
                birthDate = displayDate,
                initialProfileImageUrl = userProfile.profileImageUrl
            )
            isOnboarded = userProfile.isOnboarded
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.FirstNameChanged -> _state.value = _state.value.copy(firstName = event.firstName)
            is EditProfileEvent.LastNameChanged -> _state.value = _state.value.copy(lastName = event.lastName)
            is EditProfileEvent.UsernameChanged -> _state.value = _state.value.copy(username = event.username)
            is EditProfileEvent.BirthDateChanged -> validateBirthDate(event.birthDate)
            is EditProfileEvent.ImageSelected -> _state.value = _state.value.copy(selectedImageUri = event.uri)
            is EditProfileEvent.SaveClicked -> saveProfile(event.context)
            is EditProfileEvent.DismissError -> _state.value = _state.value.copy(error = null)
            is EditProfileEvent.DismissSuccess -> _state.value = _state.value.copy(isSuccess = false)
        }
    }

    private fun validateBirthDate(digits: String) {
        val cleanDigits = digits.filter { it.isDigit() }.take(8)
        var errorMsg: String? = null

        if (cleanDigits.length == 8) {
            try {
                val formatted = "${cleanDigits.substring(0, 2)}.${cleanDigits.substring(2, 4)}.${cleanDigits.substring(4, 8)}"
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(formatted)

                val cal = Calendar.getInstance()
                cal.add(Calendar.YEAR, -13)
                if (date == null || date.after(cal.time)) {
                    errorMsg = "En az 13 ya\u015f\u0131nda olmal\u0131s\u0131n\u0131z"
                }
            } catch (e: Exception) {
                errorMsg = "Ge\u00e7ersiz tarih format\u0131"
            }
        } else if (cleanDigits.length > 0) {
            errorMsg = "Eksik tarih"
        }

        _state.value = _state.value.copy(
            birthDate = cleanDigits,
            birthDateError = errorMsg
        )
    }

    private fun saveProfile(context: Context) {
        viewModelScope.launch {
            if (_state.value.birthDateError != null || _state.value.birthDate.length < 8) {
                _state.value = _state.value.copy(error = "L\u00fctfen ge\u00e7erli bir do\u011fum tarihi girin")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null, isSuccess = false)
            
            val currentState = _state.value
            
            var formattedBirthDate: String? = null
            if (currentState.birthDate.length == 8) {
                val d = currentState.birthDate
                formattedBirthDate = "${d.substring(4, 8)}-${d.substring(2, 4)}-${d.substring(0, 2)}"
            }

            val imagePart = currentState.selectedImageUri?.let { uri ->
                createMultipartBodyFromUri(context, uri, "profileImage")
            }

            val result = userRepository.updateUserProfile(
                firstName = currentState.firstName,
                lastName = currentState.lastName,
                username = currentState.username,
                birthDate = formattedBirthDate,
                isOnboarded = isOnboarded,
                profileImagePart = imagePart
            )

            result.onSuccess {
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            }.onFailure { exception ->
                _state.value = _state.value.copy(isLoading = false, error = exception.message ?: "Profil g\u00fcncellenirken bir hata olu\u015ftu.")
            }
        }
    }

    private fun createMultipartBodyFromUri(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val tempFile = File(context.cacheDir, "temp_profile_img_${System.currentTimeMillis()}")
            
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

sealed class EditProfileEvent {
    data class FirstNameChanged(val firstName: String) : EditProfileEvent()
    data class LastNameChanged(val lastName: String) : EditProfileEvent()
    data class UsernameChanged(val username: String) : EditProfileEvent()
    data class BirthDateChanged(val birthDate: String) : EditProfileEvent()
    data class ImageSelected(val uri: Uri?) : EditProfileEvent()
    data class SaveClicked(val context: Context) : EditProfileEvent()
    object DismissError : EditProfileEvent()
    object DismissSuccess : EditProfileEvent()
}
