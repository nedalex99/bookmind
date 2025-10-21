package com.nedalex.bookmind

import androidx.compose.ui.window.ComposeUIViewController
import com.nedalex.bookmind.app.App
import com.nedalex.bookmind.core.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}