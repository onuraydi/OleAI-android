package com.onur.oleai_android.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val apiServiceProvider: Provider<ApiService>
) : Authenticator {
    
    override fun authenticate(route: Route?, response: Response): Request? {
        // If the request that failed was already a refresh token request, give up.
        if (response.request.url.encodedPath.endsWith("/api/Auth/refresh-token")) {
            return null
        }

        synchronized(this) {
            // Try to refresh the token
            val refreshResult = runBlocking {
                try {
                    apiServiceProvider.get().refreshToken().execute()
                } catch (e: Exception) {
                    null
                }
            }

            if (refreshResult != null && refreshResult.isSuccessful) {
                // The cookies (including the new access token) are automatically saved by the CookieJar.
                // We just need to replay the request. The new request will automatically get the new cookies.
                return response.request.newBuilder().build()
            }
            
            return null
        }
    }
}
