package org.onereed.shared.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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

/** Returns true if the [permission] is needed at the current SDK level. */
fun needsPermission(permission: String): Boolean = sdkAtLeast(permToSdk.getValue(permission))

/**
 * Returns a list containing only the members of the original collection [permissions] that are
 * needed at the current SDK level.
 */
fun neededPermissions(permissions: Collection<String>): List<String> = permissions.filter {
  needsPermission(it)
}

/**
 * Returns true if the [permission] is granted. Must not be called for permissions that are not
 * needed at the current SDK level (see [needsPermission]).
 */
fun Context.hasPermission(permission: String): Boolean =
  checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

/**
 * Returns true if all [permissions] are granted. Must not be called for permissions that are not
 * needed at the current SDK level (see [needsPermission]).
 */
fun Context.hasAllPermissions(permissions: Collection<String>): Boolean = permissions.all {
  hasPermission(it)
}

/**
 * Returns true if any [permissions] indicate that the user should be shown a rationale before
 * requesting them. Must not be called for permissions that are not needed at the current SDK level
 * (see [needsPermission]).
 */
fun Activity.anyShouldShowRationale(permissions: Collection<String>): Boolean = permissions.any {
  shouldShowRequestPermissionRationale(it)
}
