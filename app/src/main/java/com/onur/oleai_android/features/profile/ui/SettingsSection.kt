package com.onur.oleai_android.features.profile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.core.network.dto.UserProfileResponse

@Composable
fun SettingsSection(
    userProfile: UserProfileResponse,
    onChangePassword: (com.onur.oleai_android.core.network.dto.ChangePasswordRequest) -> Unit,
    onLogoutClick: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordConfirm by remember { mutableStateOf("") }
    
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordConfirmVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hesap Bilgileri Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Hesap Bilgileri",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Info Rows
                AccountInfoRow(
                    icon = Icons.Outlined.Email,
                    label = "E-posta Adresi",
                    value = userProfile.email
                )

                val birthDateStr = formatApiDate(userProfile.birthDate)
                AccountInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Doğum Tarihi",
                    value = birthDateStr
                )

                val createdDateStr = formatApiDate(userProfile.createdDate)
                AccountInfoRow(
                    icon = Icons.Outlined.Schedule,
                    label = "Üyelik Tarihi",
                    value = createdDateStr
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text("E-posta Doğrulama", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                    if (userProfile.isEmailVerified) {
                        Text("Doğrulandı ✔", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Doğrulanmadı", color = Color(0xFFEF5350), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Şifre Değiştir Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Şifre Değiştir",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                PasswordTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Mevcut Şifre",
                    placeholder = "Mevcut şifreni gir",
                    isVisible = currentPasswordVisible,
                    onVisibilityChange = { currentPasswordVisible = it }
                )

                PasswordTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Yeni Şifre",
                    placeholder = "Yeni şifreni gir",
                    isVisible = newPasswordVisible,
                    onVisibilityChange = { newPasswordVisible = it },
                    isError = newPassword.isNotEmpty() && !com.onur.oleai_android.core.util.isValidPassword(newPassword),
                    errorMessage = if (newPassword.isNotEmpty() && !com.onur.oleai_android.core.util.isValidPassword(newPassword)) com.onur.oleai_android.core.util.getPasswordErrorMessage() else null
                )

                PasswordTextField(
                    value = newPasswordConfirm,
                    onValueChange = { newPasswordConfirm = it },
                    label = "Yeni Şifre (Tekrar)",
                    placeholder = "Yeni şifreni tekrar gir",
                    isVisible = newPasswordConfirmVisible,
                    onVisibilityChange = { newPasswordConfirmVisible = it },
                    isError = newPasswordConfirm.isNotEmpty() && newPasswordConfirm != newPassword,
                    errorMessage = if (newPasswordConfirm.isNotEmpty() && newPasswordConfirm != newPassword) "Şifreler eşleşmiyor" else null
                )

                val isButtonEnabled = currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        com.onur.oleai_android.core.util.isValidPassword(newPassword) &&
                        newPasswordConfirm == newPassword

                Button(
                    onClick = {
                        onChangePassword(
                            com.onur.oleai_android.core.network.dto.ChangePasswordRequest(
                                oldPassword = currentPassword,
                                newPassword = newPassword
                            )
                        )
                        // Optional: Clear fields after click
                        currentPassword = ""
                        newPassword = ""
                        newPasswordConfirm = ""
                    },
                    enabled = isButtonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color(0xFF3C4D2A),
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Şifreyi Güncelle", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Çıkış Yap Butonu
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Outlined.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Çıkış Yap", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AccountInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Outlined.VpnKey, contentDescription = null, tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                    Icon(
                        if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color(0xFF2E332A),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorCursorColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.error,
                errorTrailingIconColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

private fun formatApiDate(dateStr: String?): String {
    if (dateStr.isNullOrEmpty()) return "Belirtilmedi"
    return try {
        val datePart = dateStr.substringBefore("T")
        val parts = datePart.split("-")
        if (parts.size == 3) {
            val year = parts[0]
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            val monthName = when(month) {
                1 -> "Ocak"; 2 -> "Şubat"; 3 -> "Mart"; 4 -> "Nisan"
                5 -> "Mayıs"; 6 -> "Haziran"; 7 -> "Temmuz"; 8 -> "Ağustos"
                9 -> "Eylül"; 10 -> "Ekim"; 11 -> "Kasım"; 12 -> "Aralık"
                else -> ""
            }
            "$day $monthName $year"
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
