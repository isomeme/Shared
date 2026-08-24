package org.onereed.shared.permission

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import org.onereed.shared.navigation.settingsIntent
import org.onereed.shared.screen.BasicFrame
import timber.log.Timber

@Composable
fun PermissionGate(
  permissions: List<String>,
  grantButtonLabel: String = "Grant required access",
  rationaleTitle: String = "Permission required",
  rationaleDescription: String =
    "This feature requires additional system access to function properly.",
  rationaleOkButtonLabel: String = "Try again",
  useSettingsTitle: String = "Permission permanently denied",
  useSettingsDescription: String =
    "Access is blocked in system settings. Open settings to grant it manually.",
  useSettingsOkButtonLabel: String = "Open settings",
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val activity = LocalActivity.current!!

  val neededPermissions = neededPermissions(permissions)

  // Initial State: Check if needed permissions are already granted

  var allGranted by
    remember(neededPermissions) {
      mutableStateOf(context.hasAllPermissions(neededPermissions))
    }

  var showRationaleDialog by remember { mutableStateOf(false) }
  var showUseSettingsDialog by remember { mutableStateOf(false) }

  LaunchedEffect(key1 = allGranted) { Timber.d("Δ allGranted -> $allGranted") }
  LaunchedEffect(key1 = showRationaleDialog) {
    Timber.d("Δ showRationaleDialog -> $showRationaleDialog")
  }
  LaunchedEffect(key1 = showUseSettingsDialog) {
    Timber.d("Δ showUseSettingsDialog -> $showUseSettingsDialog")
  }

  // Automatically re-check permissions when coming back from app background/settings

  LifecycleResumeEffect(Unit) {
    val currentStatus = context.hasAllPermissions(neededPermissions)
    allGranted = currentStatus

    if (currentStatus) {
      showRationaleDialog = false
      showUseSettingsDialog = false
    }

    onPauseOrDispose {}
  }

  if (allGranted) {
    content()
    return
  }

  // Set up launchers for permission requests and navigation to app settings

  val permissionLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
      val allSuccessful = resultMap.values.all { it }
      allGranted = allSuccessful

      if (!allSuccessful) {
        val deniedPermissions = resultMap.filterValues { !it }.keys
        val shouldShowRationale = activity.anyShouldShowRationale(deniedPermissions)

        if (shouldShowRationale) {
          showRationaleDialog = true
        } else {
          showUseSettingsDialog = true
        }
      }
    }

  val settingsLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult(),
      onResult = {},
    )

  // Layout rendering

  if (showRationaleDialog) {
    StatelessPermissionDialog(
      title = rationaleTitle,
      description = rationaleDescription,
      okButtonLabel = rationaleOkButtonLabel,
      onConfirm = { permissionLauncher.launch(neededPermissions.toTypedArray()) },
      onDone = { showRationaleDialog = false },
    )
  } else if (showUseSettingsDialog) {
    StatelessPermissionDialog(
      title = useSettingsTitle,
      description = useSettingsDescription,
      okButtonLabel = useSettingsOkButtonLabel,
      onConfirm = { settingsLauncher.launch(context.settingsIntent()) },
      onDone = { showUseSettingsDialog = false },
    )
  } else {
    StatelessGrantPermissionScreen(
      grantButtonLabel = grantButtonLabel,
      onGrant = {
        if (activity.anyShouldShowRationale(neededPermissions)) {
          showRationaleDialog = true
        } else {
          permissionLauncher.launch(neededPermissions.toTypedArray())
        }
      },
    )
  }
}

@Composable
private fun StatelessPermissionDialog(
  title: String,
  description: String,
  okButtonLabel: String,
  onConfirm: () -> Unit,
  onDone: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDone,
    title = { Text(title) },
    text = { Text(description) },
    confirmButton = {
      TextButton(
        onClick = {
          onDone()
          onConfirm()
        }
      ) {
        Text(okButtonLabel)
      }
    },
    dismissButton = {
      TextButton(onClick = onDone) {
        Text(stringResource(android.R.string.cancel))
      }
    },
  )
}

@Composable
private fun StatelessGrantPermissionScreen(
  grantButtonLabel: String,
  onGrant: () -> Unit,
) {
  Button(onClick = onGrant) {
    Text(grantButtonLabel)
  }
}

@Composable
@Preview
fun StatelessPermissionDialogPreview() = BasicFrame {
  StatelessPermissionDialog("Title", "Description", "OK", {}, {})
}

@Composable
@Preview
fun StatelessGrantPermissionScreenPreview() = BasicFrame {
  StatelessGrantPermissionScreen("Grant") {}
}
