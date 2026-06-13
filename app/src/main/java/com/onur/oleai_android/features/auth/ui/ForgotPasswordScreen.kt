package com.onur.oleai_android.features.auth.ui

import android.util.Patterns
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onur.oleai_android.features.auth.ui.components.AuthButton
import com.onur.oleai_android.features.auth.ui.components.AuthTextField

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.onur.oleai_android.features.auth.viewmodel.AuthViewModel
import com.onur.oleai_android.core.network.dto.ForgotPasswordRequest
import com.onur.oleai_android.features.auth.ui.components.AuthErrorMessage

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onCodeSent: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val forgotPasswordSuccess by viewModel.forgotPasswordSuccess.collectAsState()

    LaunchedEffect(forgotPasswordSuccess) {
        if (forgotPasswordSuccess) {
            viewModel.clearState()
            onCodeSent(email)
        }
    }

    fun validate(): Boolean {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Geçerli bir e-posta adresi girin"
            return false
        }
        emailError = null
        return true
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
        // Back Button
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

        // Logo
        Text(
            text = "OleAI",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Şifremi Unuttum",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lütfen kayıtlı e-posta adresinizi girin. Size şifrenizi sıfırlamanız için 6 haneli bir doğrulama kodu göndereceğiz.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = email,
            onValueChange = { 
                email = it
                if (emailError != null) emailError = null
            },
            label = "E-posta Adresi",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isError = emailError != null,
            errorMessage = emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthErrorMessage(error = error)

        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            AuthButton(
                text = "Kodu Gönder",
                onClick = {
                    if (validate()) {
                        viewModel.forgotPassword(ForgotPasswordRequest(email = email))
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
