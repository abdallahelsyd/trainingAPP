package com.example.trainingapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trainingapp.domain.models.BookUiModel
import com.example.trainingapp.domain.models.LibraryUiState
import com.example.trainingapp.ui.theme.PlayingHighlight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // This block magically watches the scroll position and reports to the ViewModel
    LaunchedEffect(listState, uiState) {
        if (uiState is LibraryUiState.Success) {
            val items = (uiState as LibraryUiState.Success).items

            snapshotFlow { listState.layoutInfo.visibleItemsInfo }.collect { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val firstIndex = visibleItems.first().index
                    val lastIndex = visibleItems.last().index

                    // Add a buffer of 5 items to fetch ahead of the scroll
                    val maxIndex = minOf(items.size - 1, lastIndex + 5)

                    val visibleIds = (firstIndex..maxIndex).mapNotNull { index ->
                        items.getOrNull(index)?.book?.id
                    }

                    viewModel.onVisibleItemsChanged(visibleIds)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Library") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is LibraryUiState.Loading -> CircularProgressIndicator()
                is LibraryUiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is LibraryUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.book.id }) { uiModel ->
                            BookRow(uiModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookRow(uiModel: BookUiModel) {
    val backgroundColor = if (uiModel.isPlaying) PlayingHighlight else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = uiModel.book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = uiModel.book.author, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Render the AudioInfo (or a loading state if it hasn't arrived yet)
            if (uiModel.audioInfo != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "⏱ ${uiModel.audioInfo.durationString}", style = MaterialTheme.typography.labelMedium)
                    Text(text = "❤️ ${uiModel.audioInfo.likes}", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                // Secondary API is still loading for this specific row
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp))
            }
        }
    }
}