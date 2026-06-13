package com.onur.oleai_android.features.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onur.oleai_android.features.auth.ui.components.AuthButton
import com.onur.oleai_android.features.auth.ui.components.AuthTextField

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.onur.oleai_android.features.auth.viewmodel.AuthViewModel
import com.onur.oleai_android.core.network.dto.ResetPasswordRequest
import com.onur.oleai_android.features.auth.ui.components.AuthErrorMessage
import com.onur.oleai_android.core.util.isValidPassword
import com.onur.oleai_android.core.util.getPasswordErrorMessage

@Composable
fun ResetPasswordScreen(
    email: String,
    otp: String,
    onNavigateBack: () -> Unit,
    onPasswordReset: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordRepeatError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val resetPasswordSuccess by viewModel.resetPasswordSuccess.collectAsState()

    LaunchedEffect(resetPasswordSuccess) {
        if (resetPasswordSuccess) {
            viewModel.clearState()
            onPasswordReset()
        }
    }

    fun validate(): Boolean {
        var isValid = true

        if (!isValidPassword(password)) {
            passwordError = getPasswordErrorMessage()
            isValid = false
        } else passwordError = null

        if (passwordRepeat != password) {
            passwordRepeatError = "Şifreler eşleşmiyor"
            isValid = false
        } else passwordRepeatError = null

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Geri Dön",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "OleAI",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Yeni Şifre Belirle",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lütfen yeni şifrenizi girin. ${getPasswordErrorMessage()}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = password,
            onValueChange = { 
                password = it
                if (passwordError != null) passwordError = null 
            },
            label = "Yeni Şifre",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = passwordRepeat,
            onValueChange = { 
                passwordRepeat = it
                if (passwordRepeatError != null) passwordRepeatError = null 
            },
            label = "Yeni Şifre Tekrarı",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = passwordRepeatError != null,
            errorMessage = passwordRepeatError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthErrorMessage(error = error)

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            AuthButton(
                text = "Şifreyi Sıfırla",
                onClick = {
                    if (validate()) {
                        viewModel.resetPassword(
                            ResetPasswordRequest(
                                email = email,
                                otpCode = otp,
                                newPassword = password
                            )
                        )
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
