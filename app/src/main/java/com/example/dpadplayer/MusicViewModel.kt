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

    private val _position = MutableLiveData<Long>(0L)
    val position: LiveData<Long> = _position

    // OFF / ALL / ONE
    private val _repeatMode = MutableLiveData<Int>(REPEAT_OFF)
    val repeatMode: LiveData<Int> = _repeatMode

    private val _shuffleOn = MutableLiveData<Boolean>(false)
    val shuffleOn: LiveData<Boolean> = _shuffleOn

    companion object {
        const val REPEAT_OFF = 0
        const val REPEAT_ALL = 1
        const val REPEAT_ONE = 2
    }

    fun loadTracks(sortOrder: String = "title") {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MediaStoreScanner.loadTracks(getApplication(), sortOrder)
            _tracks.postValue(result)
        }
    }

    fun setCurrentIndex(index: Int) { _currentIndex.value = index }
    fun setPlaying(playing: Boolean) { _isPlaying.value = playing }
    fun setPosition(pos: Long)       { _position.value = pos }
    fun setRepeatMode(mode: Int)     { _repeatMode.value = mode }
    fun setShuffleOn(on: Boolean)    { _shuffleOn.value = on }
}
