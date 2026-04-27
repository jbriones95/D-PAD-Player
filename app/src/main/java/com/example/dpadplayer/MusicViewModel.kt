package com.example.dpadplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dpadplayer.db.AppDatabase
import com.example.dpadplayer.db.PlaylistEntity
import com.example.dpadplayer.db.PlaylistSongEntity
import com.example.dpadplayer.playback.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MusicViewModel(app: Application) : AndroidViewModel(app) {
    var activeLibraryTab = -1
    var homeMenuFocusPos = 0
    private val libraryTabFocusPositions = IntArray(5) { -1 }

    fun getLibraryTabFocusPosition(tab: Int): Int =
        libraryTabFocusPositions.getOrElse(tab) { -1 }

    fun setLibraryTabFocusPosition(tab: Int, position: Int) {
        if (tab in libraryTabFocusPositions.indices) {
            libraryTabFocusPositions[tab] = position.coerceAtLeast(0)
        }
    }

    // ── Playback state ────────────────────────────────────────────────────────

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _currentIndex = MutableLiveData<Int>(-1)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _position = MutableLiveData<Long>(0L)
    val position: LiveData<Long> = _position

    private val _repeatMode = MutableLiveData<Int>(REPEAT_OFF)
    val repeatMode: LiveData<Int> = _repeatMode

    private val _shuffleOn = MutableLiveData<Boolean>(false)
    val shuffleOn: LiveData<Boolean> = _shuffleOn

    private val _queue = MutableLiveData<List<Track>>(emptyList())
    val queue: LiveData<List<Track>> = _queue

    // ── Library (albums / artists / genres) ───────────────────────────────────

    private val _library = MutableLiveData<MusicLibrary.Library>(
        MusicLibrary.Library(emptyList(), emptyList(), emptyList(), emptyList()))
    val library: LiveData<MusicLibrary.Library> = _library

    private val _albums  = MutableLiveData<List<Album>>(emptyList())
    val albums:  LiveData<List<Album>>  = _albums

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> = _artists

    private val _genres  = MutableLiveData<List<Genre>>(emptyList())
    val genres:  LiveData<List<Genre>>  = _genres

    // ── Playlists (Room) ──────────────────────────────────────────────────────

    private val db get() = AppDatabase.getInstance(getApplication())

    val playlists: LiveData<List<PlaylistEntity>> =
        db.playlistDao().getAllPlaylists().asLiveData()

    // ── Loading ───────────────────────────────────────────────────────────────

    fun loadTracks(sortOrder: String = "title") {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MediaStoreScanner.loadTracks(getApplication(), sortOrder)
            val lib    = MusicLibrary.build(result)
            _tracks.postValue(result)
            _library.postValue(lib)
            _albums.postValue(lib.albums)
            _artists.postValue(lib.artists)
            _genres.postValue(lib.genres)
        }
    }

    // ── Playlist operations ───────────────────────────────────────────────────

    fun createPlaylist(name: String, tracks: List<Track> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = db.playlistDao().insertPlaylist(PlaylistEntity(name = name))
            if (tracks.isNotEmpty()) {
                db.playlistDao().insertSongs(tracks.mapIndexed { i, t ->
                    PlaylistSongEntity(playlistId = id, trackId = t.id, position = i)
                })
            }
        }
    }

    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.playlistDao().renamePlaylist(playlistId, newName)
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.playlistDao().deletePlaylist(playlist)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = db.playlistDao().getPlaylist(playlistId) ?: return@launch
            db.playlistDao().deletePlaylist(playlist)
        }
    }

    fun addTracksToPlaylist(playlistId: Long, newTracks: List<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = db.playlistDao().getSongsForPlaylistOnce(playlistId)
            val startPos = existing.size
            db.playlistDao().insertSongs(newTracks.mapIndexed { i, t ->
                PlaylistSongEntity(playlistId = playlistId, trackId = t.id, position = startPos + i)
            })
        }
    }

    fun rewritePlaylist(playlistId: Long, newTracks: List<Track>) {
        viewModelScope.launch(Dispatchers.IO) {
            db.playlistDao().clearPlaylist(playlistId)
            db.playlistDao().insertSongs(newTracks.mapIndexed { i, t ->
                PlaylistSongEntity(playlistId = playlistId, trackId = t.id, position = i)
            })
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            db.playlistDao().removeSongFromPlaylist(playlistId, trackId)
            // Re-compact positions
            val remaining = db.playlistDao().getSongsForPlaylistOnce(playlistId)
            db.playlistDao().clearPlaylist(playlistId)
            db.playlistDao().insertSongs(remaining.mapIndexed { i, s ->
                s.copy(rowId = 0, position = i)
            })
        }
    }

    /** Resolve a playlist's track IDs into Track objects (in order). */
    suspend fun resolvePlaylistTracks(playlistId: Long): List<Track> {
        val rows = db.playlistDao().getSongsForPlaylistOnce(playlistId)
        val trackMap = (_tracks.value ?: emptyList()).associateBy { it.id }
        return rows.mapNotNull { trackMap[it.trackId] }
    }

    fun observePlaylistTracks(playlistId: Long): LiveData<List<Track>> {
        return db.playlistDao().getSongsForPlaylist(playlistId)
            .map { rows ->
                val trackMap = (_tracks.value ?: emptyList()).associateBy { it.id }
                rows.mapNotNull { row -> trackMap[row.trackId] }
            }
            .asLiveData()
    }

    // ── Setters (called by MainActivity from service callbacks) ───────────────

    fun setCurrentIndex(index: Int) { _currentIndex.value = index }
    fun setPlaying(playing: Boolean) { _isPlaying.value = playing }
    fun setPosition(pos: Long)       { _position.value = pos }
    fun setRepeatMode(mode: Int)     { _repeatMode.value = mode }
    fun setShuffleOn(on: Boolean)    { _shuffleOn.value = on }
    fun setQueue(q: List<Track>)     { _queue.value = q }

    companion object {
        const val REPEAT_OFF = 0
        const val REPEAT_ALL = 1
        const val REPEAT_ONE = 2
    }
}
