package com.nedalex.bookmind.core.di

import com.nedalex.bookmind.app.utils.Constants
import com.nedalex.data.auth.GoogleSignInProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformAuthModule = module {
    single {
        GoogleSignInProvider(
            context = androidContext(),
            webClientId = Constants.GOOGLE_WEB_CLIENT_ID
        )
    }
}
