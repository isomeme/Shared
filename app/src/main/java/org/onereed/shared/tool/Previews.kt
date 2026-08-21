package org.onereed.shared.tool

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class DarkPreview

@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
annotation class LightPreview

@DarkPreview @LightPreview annotation class ThemePreviews