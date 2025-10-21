package com.nedalex.bookmind.core.di

import com.nedalex.bookmind.app.SupabaseProvider
import com.nedalex.bookmind.data.auth.AuthRepositoryImpl
import com.nedalex.bookmind.data.book.BookRepositoryImpl
import com.nedalex.bookmind.data.book.UserRepositoryImpl
import com.nedalex.bookmind.data.preference.PreferencesAIService
import com.nedalex.bookmind.data.preference.PreferencesRepositoryImpl
import com.nedalex.bookmind.domain.auth.AuthRepository
import com.nedalex.bookmind.domain.book.BookRepository
import com.nedalex.bookmind.domain.book.UserRepository
import com.nedalex.bookmind.domain.preference.PreferencesRepository
import com.nedalex.bookmind.presentation.features.dashboard.blocks.DashboardVM
import com.nedalex.bookmind.presentation.features.enrollment.preferences.composable.blocks.PreferencesVM
import com.nedalex.bookmind.presentation.features.enrollment.signin.blocks.LoginVM
import com.nedalex.bookmind.presentation.features.enrollment.singup.blocks.SignUpVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Client
    single { SupabaseProvider.supabaseClient() }

    // Auth
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginVM(get()) }
    viewModel { SignUpVM(get()) }

    factory<PreferencesRepository> { PreferencesRepositoryImpl(get(), get(), get()) }
    factory {
        PreferencesAIService(
            get(),
            "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"
        )
    }
    viewModel { PreferencesVM(get()) }

    factory<UserRepository> { UserRepositoryImpl(get()) }
    factory<BookRepository> { BookRepositoryImpl(get()) }
    viewModel { DashboardVM(get(), get(), get()) }
}