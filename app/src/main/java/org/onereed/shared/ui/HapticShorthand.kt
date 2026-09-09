package org.onereed.shared.ui

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.confirm() = performHapticFeedback(HapticFeedbackType.Confirm)
