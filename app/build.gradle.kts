plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.googleService)
}

android {
    namespace = "com.proyecto.tiendavirtualapp_kotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.proyecto.tiendavirtualapp_kotlin"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie)/*Animaciones*/
    implementation(libs.firebaseAuth)/*Autetenticasion con Firebase*/
    implementation(libs.firebaseDatabase)/*Base de datos con Firebase*/
    implementation(libs.imagePicker)/*Recortar IMG */
    implementation(libs.glide)/*Leer imagenes */
    implementation(libs.storage)/*subir archibos multimedia */
    implementation(libs.authGoogle) /*Iniciar sesión con google*/
    implementation(libs.ccp) /*Seleccionar nuestro código telefónico por país*/
    implementation(libs.circleImage)
    implementation(libs.photoView)
    implementation(libs.maps)
    implementation(libs.places)
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.okhttp3)
    implementation(libs.browser)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}