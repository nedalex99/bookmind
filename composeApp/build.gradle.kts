import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
                implementation(libs.androidx.compose.ui.tooling.preview)
            }
        }

        commonMain {
            dependencies {
                implementation(projects.core)
                implementation(projects.domain)
                implementation(projects.data)
                implementation(projects.presentation)

                // Compose (minimal - most comes from presentation)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)

                // Navigation
                implementation(libs.navigation.compose)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Coroutines
                implementation(libs.kotlinx.coroutines.core)

                // Supabase
                implementation(libs.supabase.auth)
                implementation(libs.supabase.postgrest)

                // Ktor (required for Supabase)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.nedalex.bookmind"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nedalex.bookmind"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}

buildkonfig {
    packageName = "com.nedalex.bookmind"

    // Read from local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPABASE_KEY", localProperties.getProperty("supabase.key", ""))
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "SUPABASE_END_POINT", localProperties.getProperty("supabase.endpoint", ""))
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "GOOGLE_WEB_CLIENT_ID", localProperties.getProperty("google.web.client.id", ""))
    }
}