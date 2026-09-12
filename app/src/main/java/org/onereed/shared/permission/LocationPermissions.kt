package org.onereed.shared.permission

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import org.onereed.shared.tool.SharedApi

/**
 * If we have explicit coarse location permission but not fine location permission, we can suggest
 * that the user allow fine location access to improve accuracy.
 */
@SharedApi fun Context.isAccuracyImprovementAvailable(): Boolean =
  isPermissionRelevant(ACCESS_COARSE_LOCATION) &&
    hasExplicitPermission(ACCESS_COARSE_LOCATION) &&
    !hasExplicitPermission(ACCESS_FINE_LOCATION)

/**
 * If fine location is available, coarse location will always also be available. So we can use
 * coarse location availability as a proxy for location availability in general.
 */
@SharedApi fun Context.hasLocationPermission(): Boolean = hasPermission(ACCESS_COARSE_LOCATION)
