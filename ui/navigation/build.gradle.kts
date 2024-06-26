plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android{
    compileSdk = 34
    namespace = "com.example.navigation"

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(project(":data:local"))
    implementation(project(":ui:service"))
    implementation(project(":ui:analysis"))
    implementation(project(":ui:overview"))
    implementation(project(":ui:myInfo"))
    implementation(project(":ui:record"))
    implementation(project(":ui:reward"))
    implementation(project(":domain:coreDomain"))
    implementation(project(":domain:AnalysisDomain"))
    implementation(project(":domain:RecordDomain"))
    implementation(project(":data:service"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    //compose viewmodel
    implementation(libs.androidx.lifecycle.viewModelCompose)

    //compose navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.testing)

    //compose material3
    implementation(libs.androidx.compose.material3)
}