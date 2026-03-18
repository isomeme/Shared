plugins {
  alias(sharedLibs.plugins.androidLibrary)
  alias(sharedLibs.plugins.kotlinComposePlugin)
}

android {
  namespace = "org.onereed.shared"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    create("staging") {}
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    isCoreLibraryDesugaringEnabled = true
  }

  buildFeatures {
    buildConfig = true
    compose = true
  }
}

dependencies {

  // Required for Java 8+ APIs on API levels < 33

  coreLibraryDesugaring(sharedLibs.desugarLib)

  implementation(sharedLibs.composeRuntimeLib)
  implementation(sharedLibs.androidx.core.ktx)
  implementation(sharedLibs.androidx.appcompat)
  implementation(sharedLibs.material)
  testImplementation(sharedLibs.junit)
  androidTestImplementation(sharedLibs.androidx.junit)
  androidTestImplementation(sharedLibs.androidx.espresso.core)
}
