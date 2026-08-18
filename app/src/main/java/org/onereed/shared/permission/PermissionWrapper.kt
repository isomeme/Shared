package org.onereed.shared.permission

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.onereed.shared.navigation.openSettings
import timber.log.Timber

@Composable
fun PermissionOrDie(permission: String) {
  if (!needsPermission(permission)) {
    Timber.d("Permission not needed at this SDK version: $permission")
    return
  }

  val context = LocalContext.current
  val activity = LocalActivity.current!!
  val lifecycleOwner = LocalLifecycleOwner.current

  var isGranted by remember { mutableStateOf(context.hasPermission(permission)) }
  var showRationaleDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }
  var hasRequestedPermission by remember { mutableStateOf(false) }

  LaunchedEffect(key1 = isGranted) { Timber.d("Δ isGranted -> $isGranted") }
  LaunchedEffect(key1 = showRationaleDialog) {
    Timber.d("Δ showRationaleDialog -> $showRationaleDialog")
  }
  LaunchedEffect(key1 = showSettingsDialog) {
    Timber.d("Δ showSettingsDialog -> $showSettingsDialog")
  }
  LaunchedEffect(key1 = hasRequestedPermission) {
    Timber.d("Δ hasRequestedPermission -> $hasRequestedPermission")
  }

  if (isGranted) {
    Timber.d("Permission granted: $permission")
    return
  }

  val launcher =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
      result ->
      isGranted = result
      hasRequestedPermission = true

      // If denied and rationale is false, they checked "Don't ask again" (or OS auto-denied)
      if (!isGranted) {
        showRationaleDialog = activity.shouldShowRequestPermissionRationale(permission)

        if (!showRationaleDialog) {
          showSettingsDialog = true
        }
      }
    }

  // 1. Soft Rationale Dialog (Standard Denial)
  if (showRationaleDialog) {
    AlertDialog(
      onDismissRequest = { showRationaleDialog = false },
      title = { Text("Permission Required") },
      text = { Text("We need a permission for this app to work.") },
      confirmButton = {
        TextButton(
          onClick = {
            showRationaleDialog = false
            launcher.launch(permission)
          }
        ) {
          Text(stringResource(android.R.string.ok))
        }
      },
      dismissButton = {
        TextButton(onClick = activity::finish) { Text(stringResource(android.R.string.cancel)) }
      },
    )
  }

  // 2. Hard Denial Dialog (Permanent Denial -> Redirects to OS Settings)
  if (showSettingsDialog) {
    AlertDialog(
      onDismissRequest = { showSettingsDialog = false },
      title = { Text("Permissions Blocked") },
      text = {
        Text(
          "We need a permission for this app to work. Please enable it in your system settings."
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            showSettingsDialog = false
            context.openSettings()
          }
        ) {
          Text(stringResource(android.R.string.ok))
        }
      },
      dismissButton = {
        TextButton(onClick = activity::finish) { Text(stringResource(android.R.string.cancel)) }
      },
    )
  }

  // Re-check permission state every time the user brings the app back to the foreground

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        isGranted = context.hasPermission(permission)
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  // Start the state machine on initial load

  LaunchedEffect(Unit) {
    val shouldShowRationale = activity.shouldShowRequestPermissionRationale(permission)

    if (shouldShowRationale) {
      showRationaleDialog = true
    } else if (hasRequestedPermission) {
      // If they clicked again after a permanent denial, skip launcher and show settings dialog
      // immediately
      showSettingsDialog = true
    } else {
      launcher.launch(permission)
    }
  }
}
