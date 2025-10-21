package com.nedalex.bookmind.domain.auth

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun signUpWithEmailAndPassword(email: String, password: String, fullName: String)
    suspend fun signOut()
}