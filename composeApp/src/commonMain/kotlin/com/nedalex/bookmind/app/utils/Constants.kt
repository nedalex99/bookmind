package com.nedalex.bookmind.app.utils

import com.nedalex.bookmind.BuildKonfig

object Constants {
    // These values are injected at build time from local.properties
    // Never commit actual credentials to version control
    val SUPABASE_KEY = BuildKonfig.SUPABASE_KEY
    val SUPABASE_END_POINT = BuildKonfig.SUPABASE_END_POINT
    val GOOGLE_WEB_CLIENT_ID = BuildKonfig.GOOGLE_WEB_CLIENT_ID
}