package com.nedalex.bookmind.core.di

import org.koin.core.module.Module

/**
 * Platform-specific authentication module.
 * Provides GoogleSignInProvider with platform-specific dependencies.
 */
expect val platformAuthModule: Module
