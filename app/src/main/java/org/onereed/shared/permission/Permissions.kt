package org.onereed.shared.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import org.onereed.shared.sysinfo.sdkAtLeast

@SuppressLint("InlinedApi")
private val permToSdk: Map<String, Int> =
  mapOf(
    Manifest.permission.ACCESS_FINE_LOCATION to Build.VERSION_CODES.M,
    Manifest.permission.ACCESS_COARSE_LOCATION to Build.VERSION_CODES.M,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION to Build.VERSION_CODES.S,
    Manifest.permission.POST_NOTIFICATIONS to Build.VERSION_CODES.TIRAMISU,
  )

fun needsPermission(permission: String): Boolean = sdkAtLeast(permToSdk.getValue(permission))

fun Context.hasPermission(permission: String): Boolean =
  !needsPermission(permission) ||
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
