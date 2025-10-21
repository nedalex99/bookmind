package com.nedalex.bookmind.app

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object EnrollmentGraph

    @Serializable
    data object SignIn

    @Serializable
    data object SignUp

    @Serializable
    data object Preferences

    // Main App Graph
    @Serializable
    data object MainGraph

    // Books
    @Serializable
    data object Dashboard

    @Serializable
    data class BookDetail(val bookId: String)

    @Serializable
    data object AddBook

    // Recommendations
    @Serializable
    data object Recommendations

    // User Library
    @Serializable
    data object MyLibrary
}