package com.nedalex.data.auth

/**
 * Platform-specific Google Sign-In provider.
 * Returns the ID token from Google Sign-In that can be used with Supabase.
 */
expect class GoogleSignInProvider {
    /**
     * Initiates Google Sign-In flow and returns the ID token.
     * @return ID token string if successful, null otherwise
     */
    suspend fun signIn(): String?
}
