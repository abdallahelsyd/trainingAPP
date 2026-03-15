package com.example.trainingapp.domain.models

data class AudioBook(
    val id: String,
    val title: String,
    val author: String
)

data class BookUiModel(
    val book: AudioBook,
    val isPlaying: Boolean,
    val audioInfo: AudioInfo?
)
data class AudioInfo(
    val likes: Int,
    val durationString: String
)

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(val items: List<BookUiModel>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}