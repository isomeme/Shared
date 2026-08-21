package org.onereed.shared.screen

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.onereed.shared.navigation.settingsIntent
import timber.log.Timber

@Composable
fun PermissionScreen(
  permission: String,
  @StringRes rationaleId: Int,
  @StringRes settingsId: Int,
  onPermissionGranted: () -> Unit,
  onDismiss: () -> Unit,
) {
  Timber.d("Start, permission = $permission")

  val context = LocalContext.current
  val activity = LocalActivity.current!!

  // We increment requestCount to trigger recomposition after each time a launched user interaction
  // returns to this composable without the user having granted permission. This provides the
  // "heartbeat" that triggers requestMode state machine changes.

  var requestCount by rememberSaveable { mutableIntStateOf(0) }
  val requestMode =
    remember(requestCount) {
      when {
        activity.shouldShowRequestPermissionRationale(permission) -> RequestMode.SHOW_RATIONALE
        requestCount > 0 -> RequestMode.SEND_TO_SETTINGS
        else -> RequestMode.ASK_DIRECTLY
      }
    }

  LaunchedEffect(key1 = requestCount) { Timber.d("Δ requestCount -> $requestCount") }
  LaunchedEffect(key1 = requestMode) { Timber.d("Δ requestMode -> $requestMode") }

  val permissionRequestLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestPermission(),
      onResult = { isGranted ->
        if (isGranted) {
          onPermissionGranted()
        } else {
          requestCount += 1
        }
      },
    )

  val settingsLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult(),
      onResult = { requestCount += 1 },
    )

  when (requestMode) {
    RequestMode.ASK_DIRECTLY ->
      // The user has not previously been asked to grant the permission. Show them the normal system
      // permission dialog over an otherwise blank screen. The launch callback is guaranteed to
      // either (1) trigger a TopLevelScreen recomposition that makes this screen irrelevant, or
      // (2) update requestCount in a way that makes this state unreachable.

      LaunchedEffect(Unit) { permissionRequestLauncher.launch(permission) }

    RequestMode.SHOW_RATIONALE ->
      // The user has denied the permission previously, but Android will still show them the normal
      // system permission dialog. Explain why we need the permission, and provide an opportunity to
      // grant it.

      StatelessPermissionScreen(
        textId = rationaleId,
        onConfirm = { permissionRequestLauncher.launch(permission) },
        onDismiss = onDismiss,
      )

    RequestMode.SEND_TO_SETTINGS ->
      // Android will no longer show the user the normal system permission dialog. Explain that they
      // can grant the permission in the app's settings, and provide an opportunity to go there.

      StatelessPermissionScreen(
        textId = settingsId,
        onConfirm = { settingsLauncher.launch(context.settingsIntent()) },
        onDismiss = onDismiss,
      )
  }

  Timber.d("End")
}

@Composable
private fun StatelessPermissionScreen(
  @StringRes textId: Int,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  // We set the two onDismissRequest callbacks to empty lambdas to make the dialog non-cancelable.

  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
  ) {
    AlertDialog(
      onDismissRequest = {},
      text = { Text(text = stringResource(textId)) },
      confirmButton = {
        TextButton(onClick = onConfirm) { Text(text = stringResource(android.R.string.ok)) }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) { Text(text = stringResource(android.R.string.cancel)) }
      },
    )
  }
}

private enum class RequestMode {
  ASK_DIRECTLY,
  SHOW_RATIONALE,
  SEND_TO_SETTINGS,
}
