package com.onur.oleai_android.core.util

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (!password.any { it.isUpperCase() }) return false
    if (!password.any { it.isLowerCase() }) return false
    if (!password.any { !it.isLetterOrDigit() }) return false
    return true
}

fun getPasswordErrorMessage(): String {
    return "Şifre en az 8 karakter olmalı, büyük/küçük harf ve noktalama işareti içermelidir"
}
