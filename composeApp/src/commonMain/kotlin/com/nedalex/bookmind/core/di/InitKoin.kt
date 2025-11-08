package com.nedalex.bookmind.core.di

import com.nedalex.data.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
        modules(platformModule)
        modules(platformAuthModule)
    }
}