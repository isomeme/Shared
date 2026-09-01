package org.onereed.shared.permission

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context

// Because location permissions are unique in having two versions (coarse and fine) that "stack",
// we provide special handling for this case.

enum class LocationPermissionState {
  NOT_REQUIRED,
  NOT_GRANTED,
  COARSE_ONLY,
  COARSE_AND_FINE,
}

fun Context.getLocationPermissionState(): LocationPermissionState =
  when {
    !isPermissionRelevant(ACCESS_COARSE_LOCATION) -> LocationPermissionState.NOT_REQUIRED
    !isPermissionGranted(ACCESS_COARSE_LOCATION) -> LocationPermissionState.NOT_GRANTED
    !isPermissionGranted(ACCESS_FINE_LOCATION) -> LocationPermissionState.COARSE_ONLY
    else -> LocationPermissionState.COARSE_AND_FINE
  }
