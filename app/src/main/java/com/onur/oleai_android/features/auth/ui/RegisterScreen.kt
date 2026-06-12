package com.onur.oleai_android.features.auth.ui

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.onur.oleai_android.features.auth.ui.components.AuthButton
import com.onur.oleai_android.features.auth.ui.components.AuthTextField
import com.onur.oleai_android.features.auth.viewmodel.AuthViewModel
import com.onur.oleai_android.core.network.dto.RegisterRequest
import com.onur.oleai_android.features.auth.ui.components.AuthErrorMessage

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordRepeatError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf(false) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            onRegisterSuccess()
        }
    }

    val isFormValid = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 6 &&
            passwordRepeat == password &&
            termsAccepted

    fun validate(): Boolean {
        var isValid = true
        if (firstName.isBlank()) {
            firstNameError = "İsim alanı boş bırakılamaz"
            isValid = false
        } else firstNameError = null

        if (lastName.isBlank()) {
            lastNameError = "Soyisim alanı boş bırakılamaz"
            isValid = false
        } else lastNameError = null

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Geçerli bir e-posta adresi girin"
            isValid = false
        } else emailError = null

        if (password.length < 6) {
            passwordError = "Şifre en az 6 karakter olmalıdır"
            isValid = false
        } else passwordError = null

        if (passwordRepeat != password) {
            passwordRepeatError = "Şifreler eşleşmiyor"
            isValid = false
        } else passwordRepeatError = null

        if (!termsAccepted) {
            termsError = true
            isValid = false
        } else termsError = false

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
        Spacer(modifier = Modifier.height(80.dp))
        
        // Logo
        Text(
            text = "OleAI",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        
        // Slogan
        Text(
            text = "Kişisel Yapay Zeka Şefiniz",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // Name Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuthTextField(
                value = firstName,
                onValueChange = { 
                    firstName = it
                    if (firstNameError != null) firstNameError = null 
                },
                label = "İsim",
                leadingIcon = Icons.Filled.Person,
                isError = firstNameError != null,
                errorMessage = firstNameError,
                modifier = Modifier.weight(1f)
            )

            AuthTextField(
                value = lastName,
                onValueChange = { 
                    lastName = it
                    if (lastNameError != null) lastNameError = null 
                },
                label = "Soyisim",
                leadingIcon = null,
                isError = lastNameError != null,
                errorMessage = lastNameError,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
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

        // Password Field
        AuthTextField(
            value = password,
            onValueChange = { 
                password = it
                if (passwordError != null) passwordError = null 
            },
            label = "Şifre",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Repeat Field
        AuthTextField(
            value = passwordRepeat,
            onValueChange = { 
                passwordRepeat = it
                if (passwordRepeatError != null) passwordRepeatError = null 
            },
            label = "Şifre Tekrarı",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isError = passwordRepeatError != null,
            errorMessage = passwordRepeatError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Terms and Conditions Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { 
                    termsAccepted = it
                    if (termsError) termsError = false 
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = if (termsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Column {
                Row {
                    Text(
                        text = "Kullanım Şartları",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showTermsDialog = true }
                    )
                    Text(text = " ve ", color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = "Gizlilik Politikasını",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showPrivacyDialog = true }
                    )
                }
                Text(
                    text = " okudum, onaylıyorum.", 
                    color = if (termsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthErrorMessage(error = error)

        // Register Button
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            AuthButton(
                text = "Kayıt Ol",
                enabled = true,
                onClick = {
                    if (validate()) {
                        viewModel.register(
                            RegisterRequest(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password
                            )
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row {
            Text(
                text = "Zaten hesabınız var mı? ",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = "Giriş Yap",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onNavigateToLogin)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }

    // Dialogs for Legal Texts
    if (showTermsDialog) {
        LegalDialog(
            title = "Kullanım Şartları",
            text = LegalTexts.termsOfUse,
            onDismiss = { showTermsDialog = false }
        )
    }

    if (showPrivacyDialog) {
        LegalDialog(
            title = "Gizlilik Politikası",
            text = LegalTexts.privacyPolicy,
            onDismiss = { showPrivacyDialog = false }
        )
    }
}

@Composable
fun LegalDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = text, fontSize = 14.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
