package com.example.dpadplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dpadplayer.playback.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel(app: Application) : AndroidViewModel(app) {

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _currentIndex = MutableLiveData<Int>(-1)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    fun loadTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MediaStoreScanner.loadTracks(getApplication())
            _tracks.postValue(result)
        }
    }

    fun setCurrentIndex(index: Int) { _currentIndex.value = index }
    fun setPlaying(playing: Boolean) { _isPlaying.value = playing }
}
