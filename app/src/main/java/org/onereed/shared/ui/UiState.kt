package org.onereed.shared.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

sealed interface UiState<out T> {
  object Loading : UiState<Nothing>

  data class Error(val message: String) : UiState<Nothing>

  data class Success<T>(val data: T) : UiState<T>
}

@Composable
fun <T> UiStateContent(
  state: UiState<T>,
  modifier: Modifier = Modifier,
  loadingContent: @Composable () -> Unit = { DefaultLoadingContent() },
  errorContent: @Composable (String) -> Unit = { DefaultErrorContent(it) },
  content: @Composable (T) -> Unit,
) {
  Box(modifier = modifier) {
    when (state) {
      is UiState.Loading -> loadingContent()
      is UiState.Error -> errorContent(state.message)
      is UiState.Success -> content(state.data)
    }
  }
}

@Composable
fun DefaultLoadingContent() {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}

@Composable
fun DefaultErrorContent(message: String) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(text = message, color = MaterialTheme.colorScheme.error)
  }
}

@Preview
@Composable
fun DefaultLoadingContentPreview() {
  BasicFrame { DefaultLoadingContent() }
}

@Preview
@Composable
fun DefaultErrorContentPreview() {
  BasicFrame { DefaultErrorContent("Error message") }
}
