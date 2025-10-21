package com.nedalex.bookmind.app

import com.nedalex.bookmind.app.utils.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {
    fun supabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = Constants.SUPABASE_END_POINT,
            supabaseKey = Constants.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}