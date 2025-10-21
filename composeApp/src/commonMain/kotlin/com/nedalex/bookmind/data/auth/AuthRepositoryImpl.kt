package com.nedalex.bookmind.data.auth

import com.nedalex.bookmind.domain.auth.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val client: SupabaseClient
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

    override suspend fun signOut() {
        client.auth.signOut()
    }
}