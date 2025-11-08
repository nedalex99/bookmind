package com.nedalex.bookmind.core.di

import com.nedalex.bookmind.app.SupabaseProvider
import com.nedalex.data.auth.AuthRepositoryImpl
import com.nedalex.domain.auth.AuthRepository
import com.nedalex.presentation.features.enrollment.resetpassword.blocks.ResetPasswordVM
import com.nedalex.presentation.features.enrollment.signin.blocks.LoginVM
import com.nedalex.presentation.features.enrollment.singup.blocks.SignUpVM
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Client
    single<SupabaseClient> { SupabaseProvider.supabaseClient() }

    // Auth
    factory<AuthRepository> {
        AuthRepositoryImpl(
            client = get(),
            googleSignInProvider = get()
        )
    }
    viewModel { LoginVM(get()) }
    viewModel { SignUpVM(get()) }
    viewModel { ResetPasswordVM(get()) }

}