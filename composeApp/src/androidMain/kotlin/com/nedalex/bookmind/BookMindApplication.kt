package com.nedalex.bookmind

import android.app.Application
import com.nedalex.bookmind.core.di.appModule
import com.nedalex.bookmind.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BookMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@BookMindApplication)
            modules(appModule)
        }
    }
}