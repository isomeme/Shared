plugins {
  alias(sharedLibs.plugins.androidLibrary)
  alias(sharedLibs.plugins.kotlinComposePlugin)
}

android {
  namespace = "org.onereed.shared"
  group = "org.onereed.shared"
  version = "1.0"
  compileSdk { version = release(37) { minorApiLevel = 1 } }

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

  // Compose BOM
  // See https://www.reddit.com/r/AndroidStudio/comments/1vnjxv4/comment/p3mmorh/
  @Suppress("AvoidDuplicateDependencies")
  implementation(platform(libs.composeBomLib))
  @Suppress("AvoidDuplicateDependencies")
  androidTestImplementation(platform(libs.composeBomLib))

  implementation(sharedLibs.accompanistPermissions)
  implementation(sharedLibs.activityComposeLib)
  implementation(sharedLibs.composeRuntimeLib)
  implementation(sharedLibs.androidx.core.ktx)
  implementation(sharedLibs.androidx.appcompat)
  implementation(sharedLibs.gmsTasks)
  implementation(sharedLibs.guavaLib)
  implementation(sharedLibs.material)
  implementation(sharedLibs.material3Lib)
  implementation(sharedLibs.timber)
  testImplementation(sharedLibs.junit)
  androidTestImplementation(sharedLibs.androidx.junit)
  androidTestImplementation(sharedLibs.androidx.espresso.core)
}
