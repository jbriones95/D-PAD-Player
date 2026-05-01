package com.example.dpadplayer

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.File

data class ArtworkEvent(val albumId: Long, val trackId: Long, val uri: Uri)

object ArtRepository {
    private val _events = MutableSharedFlow<ArtworkEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<ArtworkEvent> get() = _events

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun publish(event: ArtworkEvent) {
        // emit asynchronously so callers on IO threads don't suspend
        scope.launch { _events.emit(event) }
    }

    /** Return cached album art Uri (album_<albumId>.jpg) if present */
    fun getCachedAlbumArt(context: Context, albumId: Long): Uri? {
        if (albumId <= 0) return null
        val dir = File(context.cacheDir, "album_art")
        val f = File(dir, "album_$albumId.jpg")
        return if (f.exists()) Uri.fromFile(f) else null
    }
}
