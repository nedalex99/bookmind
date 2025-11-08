package com.nedalex.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Android implementation of Google Sign-In using Credential Manager API.
 * This provides a more modern and streamlined approach to Google Sign-In.
 */
actual class GoogleSignInProvider(
    private val context: Context,
    private val webClientId: String
) {
    companion object {
        private const val TAG = "GoogleSignInProvider"
    }

    /**
     * Initiates Google Sign-In flow using Android Credential Manager.
     * @return Google ID token if successful, null if sign-in fails or is cancelled
     */
    actual suspend fun signIn(): String? {
        Log.d(TAG, "Starting Google Sign-In flow")
        Log.d(TAG, "Web Client ID: ${webClientId.take(20)}...")

        return try {
            val credentialManager = CredentialManager.create(context)

            // Generate a nonce for security (optional but recommended)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting credentials...")
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            Log.d(TAG, "Credential received: ${result.credential.type}")
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            Log.d(TAG, "Successfully obtained ID token")
            googleIdTokenCredential.idToken

        } catch (e: NoCredentialException) {
            Log.e(TAG, "No credentials available: ${e.message}")
            Log.e(TAG, "Error type: ${e.type}")
            Log.e(TAG, "This usually means:")
            Log.e(TAG, "1. Android OAuth client not created or not propagated yet (wait 5-10 min)")
            Log.e(TAG, "2. SHA-1 fingerprint: 05:A2:7C:4A:26:3A:85:72:6E:A7:B3:49:EA:25:0F:2F:0C:B7:C8:78")
            Log.e(TAG, "3. Package name: com.nedalex.bookmind")
            Log.e(TAG, "4. Web Client ID: ${webClientId}")
            Log.e(TAG, "5. No Google account on device")
            e.printStackTrace()
            null
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled the sign-in")
            null
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential error: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Invalid token format: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
