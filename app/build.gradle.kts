plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.molytech.fsa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.molytech.fsa"
        minSdk = 24
        targetSdk = 35
        versionCode = 7
        versionName = "1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuración para símbolos de depuración nativos
        ndk {
            debugSymbolLevel = "FULL"
            // Especificar ABIs compatibles con 16 KB
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Configuración específica para símbolos de depuración en release
            isDebuggable = false
            isJniDebuggable = false
            isPseudoLocalesEnabled = false

            // Generar símbolos de depuración para Google Play
            ndk {
                debugSymbolLevel = "FULL"
            }

            // Asegurar que se generen símbolos nativos separados
            packaging {
                jniLibs {
                    keepDebugSymbols += "**/arm64-v8a/*.so"
                    keepDebugSymbols += "**/armeabi-v7a/*.so"
                    keepDebugSymbols += "**/x86/*.so"
                    keepDebugSymbols += "**/x86_64/*.so"
                }
            }
        }

        debug {
            isDebuggable = true
            isJniDebuggable = true

            // También generar símbolos para debug builds
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    // Configuración para generar mapeo de símbolos
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }

    // Configuración para compatibilidad con dispositivos de 16 KB
    packaging {
        jniLibs {
            useLegacyPackaging = false
            // Excluir librerías nativas de Fresco problemáticas
            excludes += "lib/arm64-v8a/libimagepipeline.so"
            excludes += "lib/arm64-v8a/libnative-filters.so"
            excludes += "lib/arm64-v8a/libnative-imagetranscoder.so"
            excludes += "lib/armeabi-v7a/libimagepipeline.so"
            excludes += "lib/armeabi-v7a/libnative-filters.so"
            excludes += "lib/armeabi-v7a/libnative-imagetranscoder.so"
        }

        // Configurar exclusiones para optimización
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    // Configuración específica para Android Gradle Plugin 8.0+
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

// Configuración para forzar la versión correcta de SoLoader
configurations.all {
    resolutionStrategy {
        force("com.facebook.soloader:soloader:0.11.0")
    }
}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.osmdroid.android)

    // ViewModel y Lifecycle - usando referencias del TOML
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Corrutinas - usando referencias del TOML
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Activity ViewModel extension - usando referencias del TOML
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.cloudinary.android)
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    implementation(libs.play.services.auth)

    // SoLoader actualizado para resolver problemas de compatibilidad con dispositivos de 64 bits
    implementation(libs.soloader)

}