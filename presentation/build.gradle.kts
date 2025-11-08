plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {

    androidLibrary {
        namespace = "com.nedalex.presentation"
        compileSdk = 36
        minSdk = 24
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core)
                implementation(projects.domain)

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Lifecycle & ViewModel
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)

                // Navigation
                implementation(libs.navigation.compose)

                // Coroutines
                implementation(libs.kotlinx.coroutines.core)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Coil
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.compose.ui.tooling.preview)
                implementation(libs.androidx.compose.ui.tooling)
                implementation("androidx.customview:customview-poolingcontainer:1.1.0")
                implementation("androidx.emoji2:emoji2:1.6.0")
                implementation("androidx.emoji2:emoji2-views-helper:1.6.0")

            }
        }
    }
}