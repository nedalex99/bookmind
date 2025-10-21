package com.nedalex.bookmind.domain.sharedprefs

interface LocalSharedPreferences {
    fun getString(key: String, defaultValue: String? = null): String?
    fun putString(key: String, value: String)
}