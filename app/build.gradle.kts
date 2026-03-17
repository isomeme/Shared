plugins { alias(sharedLibs.plugins.androidLibrary) }

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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  implementation(sharedLibs.androidx.core.ktx)
  implementation(sharedLibs.androidx.appcompat)
  implementation(sharedLibs.material)
  testImplementation(sharedLibs.junit)
  androidTestImplementation(sharedLibs.androidx.junit)
  androidTestImplementation(sharedLibs.androidx.espresso.core)
}
