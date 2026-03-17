@file:Suppress("unused", "RedundantSuppression")

package org.onereed.shared

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.settingsIntent(): Intent =
  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", packageName, /* fragment= */ null)
  }
