package org.onereed.shared.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A basic layout frame for simple screens and previews. It renders [content] centered in a [Box]
 * which has already applied the inner padding from a [Scaffold].
 *
 * Do not use this class if [content] specifies its own [Scaffold].
 */
@Composable
fun BasicFrame(content: @Composable BoxScope.() -> Unit) {
  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.Center,
    ) {
      content()
    }
  }
}
