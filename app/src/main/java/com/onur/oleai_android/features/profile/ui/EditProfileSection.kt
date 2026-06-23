package com.onur.oleai_android.features.profile.ui

import androidx.compose.material3.MaterialTheme
import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.features.profile.viewmodel.EditProfileEvent
import com.onur.oleai_android.features.profile.viewmodel.EditProfileViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSection(
    userProfile: UserProfileResponse?,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userProfile) {
        userProfile?.let {
            viewModel.initData(it)
        }
    }

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    // Parse initial date if possible, else use current
    // Parse initial date if possible, else use current
    if (state.birthDate.length >= 8) {
        try {
            calendar.set(Calendar.DAY_OF_MONTH, state.birthDate.substring(0, 2).toInt())
            calendar.set(Calendar.MONTH, state.birthDate.substring(2, 4).toInt() - 1)
            calendar.set(Calendar.YEAR, state.birthDate.substring(4, 8).toInt())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val formattedDate = String.format(Locale.getDefault(), "%02d%02d%04d", dayOfMonth, month + 1, year)
            viewModel.onEvent(EditProfileEvent.BirthDateChanged(formattedDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        val maxCal = Calendar.getInstance()
        maxCal.add(Calendar.YEAR, -13)
        datePicker.maxDate = maxCal.timeInMillis
    }

    // Image Picker Logic
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onEvent(EditProfileEvent.ImageSelected(uri))
        }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("Ki\u015fisel Bilgiler", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Profile Photo Section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2E332A)),
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedImageUri != null) {
                    AsyncImage(
                        model = state.selectedImageUri,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!state.initialProfileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = state.initialProfileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = state.firstName.take(1).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Profil Foto\u011fraf\u0131", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Text("JPG, PNG veya GIF \u00b7 Maks. 5MB", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Text(
                    text = "Foto\u011fraf De\u011fi\u015ftir",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(top = 4.dp)
                )
            }
        }

        // Form Fields
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { viewModel.onEvent(EditProfileEvent.FirstNameChanged(it)) },
                label = { Text("Ad") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF2E332A),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.lastName,
                onValueChange = { viewModel.onEvent(EditProfileEvent.LastNameChanged(it)) },
                label = { Text("Soyad") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF2E332A),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.onEvent(EditProfileEvent.UsernameChanged(it)) },
                label = { Text("Kullan\u0131c\u0131 Ad\u0131") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF2E332A),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = state.birthDate,
                onValueChange = { viewModel.onEvent(EditProfileEvent.BirthDateChanged(it)) },
                readOnly = false,
                label = { Text("Do\u011fum Tarihi") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                visualTransformation = com.onur.oleai_android.features.onboarding.ui.DateVisualTransformation(),
                isError = state.birthDateError != null,
                supportingText = {
                    if (state.birthDateError != null) {
                        Text(state.birthDateError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFF2E332A),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Error & Success Messages
        if (state.error != null) {
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        if (state.isSuccess) {
            Text(
                text = "Profil ba\u015far\u0131yla g\u00fcncellendi!",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }

        // Save Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(
                onClick = { viewModel.onEvent(EditProfileEvent.SaveClicked(context)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5D34)),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                } else {
                    Text("De\u011fi\u015fiklikleri Kaydet", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
