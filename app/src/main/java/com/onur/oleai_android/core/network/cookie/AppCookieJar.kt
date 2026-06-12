package com.onur.oleai_android.core.network.cookie

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class AppCookieJar(context: Context) : CookieJar {
    private val prefs: SharedPreferences = context.getSharedPreferences("CookiePrefs", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = prefs.edit()
        val existingCookies = prefs.getStringSet(url.host, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        
        cookies.forEach { cookie ->
            // Remove old cookie with same name
            existingCookies.removeAll { it.startsWith("${cookie.name}=") }
            existingCookies.add(cookie.toString())
        }
        
        editor.putStringSet(url.host, existingCookies)
        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStrings = prefs.getStringSet(url.host, setOf()) ?: setOf()
        val validCookies = mutableListOf<Cookie>()
        val validStrings = mutableSetOf<String>()
        var hasExpired = false

        for (cookieString in cookieStrings) {
            val cookie = Cookie.parse(url, cookieString)
            if (cookie != null) {
                if (cookie.expiresAt > System.currentTimeMillis()) {
                    validCookies.add(cookie)
                    validStrings.add(cookieString)
                } else {
                    hasExpired = true
                }
            }
        }

        if (hasExpired) {
            prefs.edit().putStringSet(url.host, validStrings).apply()
        }

        return validCookies
    }

    fun clearCookies() {
        prefs.edit().clear().apply()
    }
}
