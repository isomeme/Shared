@file:Suppress("unused", "RedundantSuppression")

package org.onereed.shared.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.openSettings() {
  startActivity(settingsIntent())
}

fun Context.settingsIntent(): Intent =
  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", packageName, /* fragment= */ null)
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }
