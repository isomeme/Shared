package org.onereed.shared.permission

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import org.onereed.shared.sysinfo.sdkAtLeast

/** A map from permissions to the SDK level at which they were introduced. */
@SuppressLint("InlinedApi")
private val permToSdk: Map<String, Int> =
  mapOf(
    ACCESS_FINE_LOCATION to M,
    ACCESS_COARSE_LOCATION to M,
    POST_NOTIFICATIONS to TIRAMISU,
  )

/** Returns true if the [permission] is relevant at the current SDK level. */
fun isPermissionRelevant(permission: String): Boolean = sdkAtLeast(permToSdk.getValue(permission))

/**
 * Returns a set containing only the members of [permissions] that are relevant at the current SDK
 * level.
 */
fun onlyRelevantPermissions(permissions: Collection<String>): Set<String> =
  permissions
    .filter {
      isPermissionRelevant(it)
    }
    .toSet()

/**
 * Returns true if [permission] is granted. Must not be called for permissions that are not relevant
 * at the current SDK level (see [isPermissionRelevant]).
 */
fun Context.isPermissionGranted(permission: String): Boolean =
  checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Returns true if all [permissions] are granted. Must not be called for permissions that are not
 * relevant at the current SDK level (see [isPermissionRelevant]).
 */
fun Context.allPermissionsGranted(permissions: Collection<String>): Boolean = permissions.all {
  isPermissionGranted(it)
}

/**
 * Returns true if the capability represented by [permission] is available. This is true if either
 * the permission does not exist at the current SDK level, or the permission has been granted by the
 * user.
 */
fun Context.hasCapability(permission: String): Boolean =
  !isPermissionRelevant(permission) || isPermissionGranted(permission)

/**
 * Returns true if any [permissions] indicate that the user should be shown a rationale before
 * requesting them. Must not be called for permissions that are not required at the current SDK
 * level (see [isPermissionRelevant]).
 */
fun Activity.anyShouldShowRationale(permissions: Collection<String>): Boolean = permissions.any {
  shouldShowRequestPermissionRationale(this, it)
}
