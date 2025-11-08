package com.nedalex.data.auth

import com.nedalex.domain.auth.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val client: SupabaseClient,
    private val googleSignInProvider: GoogleSignInProvider
) : AuthRepository {
    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String, fullName: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }

    override suspend fun signInWithGoogle() {
        // Step 1: Get ID token from native Google Sign-In
        val idToken = googleSignInProvider.signIn()
            ?: throw Exception("Google Sign-In failed or was cancelled")

        // Step 2: Authenticate with Supabase using the ID token
        client.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }
    }

    override suspend fun resetPassword(email: String) {
        client.auth.resetPasswordForEmail(email)
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }
}