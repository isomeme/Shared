package org.onereed.shared.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * The base layout elements for screens. Meant to sit between the theme container and app-specific
 * content.
 */
@Composable
fun BaseScreen(content: @Composable BoxScope.() -> Unit) {
  Surface(modifier = Modifier.fillMaxSize()) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
      Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center,
        content = content,
      )
    }
  }
}
