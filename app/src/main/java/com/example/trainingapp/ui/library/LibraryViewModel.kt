package com.example.trainingapp.ui.library

import androidx.lifecycle.ViewModel
import com.example.trainingapp.data.repository.LibraryRepository
import com.example.trainingapp.domain.models.AudioInfo
import com.example.trainingapp.services.PlaybackService
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.domain.models.BookUiModel
import com.example.trainingapp.domain.models.LibraryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository,
    playbackService: PlaybackService
): ViewModel() {
    private val isRefreshing = MutableStateFlow(false)

    private val _audioInfoMap = MutableStateFlow<Map<String, AudioInfo>>(emptyMap())

    private val _fetchingIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        loadData()
    }
    private fun loadData(){
        viewModelScope.launch {
            isRefreshing.value = true
            try {
                repository.loadInitialDataIfNeeded()
            }catch (e: Exception){
                //handle error
            }finally {
                isRefreshing.value=false
            }
        }
    }

    fun onVisibleItemsChanged(visibleIds: List<String>){
        val idsToFetch = visibleIds.filter {
            !_audioInfoMap.value.containsKey(it) && !_fetchingIds.value.contains(it)
        }
        if (idsToFetch.isEmpty()) return
        viewModelScope.launch {
            idsToFetch.map { id->
                async {
                    try {
                        val info =repository.getAudioInfo(id)
                        _audioInfoMap.update { it + ( id to info) }
                    }catch (e: Exception){
                        // handle error
                    }finally {
                        _fetchingIds.update { it - id }
                    }
                }
            }.awaitAll()
        }
    }

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.getLibraryStream(),
        playbackService.currentPlayingBookId,
        _audioInfoMap,
        isRefreshing
    ){ books, playingId, infoMap, isRefreshing ->
        if (books.isEmpty() && isRefreshing) return@combine LibraryUiState.Loading
        if (books.isEmpty()) return@combine LibraryUiState.Success(emptyList())
        val uiState = books.map {
            BookUiModel(
                book = it,
                isPlaying = playingId==it.id,
                audioInfo = infoMap[it.id]
            )
        }
        LibraryUiState.Success(uiState)
    }.catch {
        emit(LibraryUiState.Error(it.localizedMessage ?: "Unknown Error"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LibraryUiState.Loading)

}