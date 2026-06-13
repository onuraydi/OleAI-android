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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onur.oleai_android.features.auth.ui.components.AuthButton
import com.onur.oleai_android.features.auth.ui.components.AuthTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.TextButton
import kotlinx.coroutines.delay

@Composable
fun VerifyCodeScreen(
    email: String,
    onNavigateBack: () -> Unit,
    onCodeVerified: (String) -> Unit,
    onResendCode: () -> Unit = {}
) {
    var code by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }
    var timeLeft by remember { mutableStateOf(60) }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    fun validate(): Boolean {
        if (code.isBlank() || code.length < 6) {
            codeError = "Lütfen 6 haneli kodu eksiksiz girin"
            return false
        }
        codeError = null
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
            text = "Kodu Doğrula",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${email} adresine gönderilen 6 haneli doğrulama kodunu girin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = code,
            onValueChange = { 
                if (it.length <= 6) {
                    code = it.filter { char -> char.isDigit() }
                    if (codeError != null) codeError = null
                }
            },
            label = "6 Haneli Kod",
            leadingIcon = Icons.Filled.Lock,
            keyboardType = KeyboardType.NumberPassword,
            isError = codeError != null,
            errorMessage = codeError
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthButton(
            text = "Doğrula",
            onClick = {
                if (validate()) {
                    onCodeVerified(code)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (timeLeft == 0) {
                    onResendCode()
                    timeLeft = 60
                }
            },
            enabled = timeLeft == 0
        ) {
            Text(
                text = if (timeLeft > 0) "Kodu Tekrar Gönder ($timeLeft sn)" else "Kodu Tekrar Gönder",
                color = if (timeLeft > 0) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
