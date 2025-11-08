package com.nedalex.core

expect fun getPlatform(): Platform

enum class Platform {
    Android,
    iOS
}