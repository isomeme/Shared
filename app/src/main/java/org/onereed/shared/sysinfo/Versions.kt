package org.onereed.shared.sysinfo

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

// Utility methods for checks based on app build SDK version.

@ChecksSdkIntAtLeast(parameter = 0)
fun sdkAtLeast(version: Int): Boolean = Build.VERSION.SDK_INT >= version

val dynamicThemeSupported = sdkAtLeast(Build.VERSION_CODES.S)
