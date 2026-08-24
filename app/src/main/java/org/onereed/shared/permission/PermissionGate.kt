package org.onereed.shared.permission

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
  allowCoarseLocation: Boolean = false,
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
  require(
    !permissions.contains(ACCESS_FINE_LOCATION) || permissions.contains(ACCESS_COARSE_LOCATION)
  ) {
    "ACCESS_COARSE_LOCATION must also be requested when ACCESS_FINE_LOCATION is requested."
  }

  val context = LocalContext.current
  val activity = LocalActivity.current!!

  val relevantPermissions = onlyRelevantPermissions(permissions)
  val requiredPermissions =
    if (allowCoarseLocation) {
      relevantPermissions - ACCESS_FINE_LOCATION
    } else {
      relevantPermissions
    }

  // Initial State: Check if needed permissions are already granted

  var allGranted by
    remember(requiredPermissions) {
      mutableStateOf(context.allPermissionsGranted(requiredPermissions))
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
    val currentStatus = context.allPermissionsGranted(requiredPermissions)
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
      val permissionsGranted = resultMap.filterValues { it }.keys
      val requiredDenied = requiredPermissions - permissionsGranted
      allGranted = requiredDenied.isEmpty()

      if (!requiredDenied.isEmpty()) {
        val shouldShowRationale = activity.anyShouldShowRationale(requiredDenied)

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
      onConfirm = { permissionLauncher.launch(relevantPermissions.toTypedArray()) },
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
        if (activity.anyShouldShowRationale(relevantPermissions)) {
          showRationaleDialog = true
        } else {
          permissionLauncher.launch(relevantPermissions.toTypedArray())
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
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Button(onClick = onGrant) {
      Text(grantButtonLabel)
    }
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
