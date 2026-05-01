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

import com.example.dpadplayer.playback.Track

data class ArtworkEvent(val albumId: Long, val trackId: Long, val uri: Uri)
data class MetadataEvent(val trackId: Long, val enriched: Track)

object ArtRepository {
    private val _artEvents = MutableSharedFlow<ArtworkEvent>(extraBufferCapacity = 8)
    val artEvents: SharedFlow<ArtworkEvent> get() = _artEvents

    private val _metaEvents = MutableSharedFlow<MetadataEvent>(extraBufferCapacity = 8)
    val metaEvents: SharedFlow<MetadataEvent> get() = _metaEvents

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun publishArtwork(event: ArtworkEvent) {
        scope.launch { _artEvents.emit(event) }
    }

    fun publishMetadata(event: MetadataEvent) {
        scope.launch { _metaEvents.emit(event) }
    }

    /** Return cached album art Uri (album_<albumId>.jpg) if present */
    fun getCachedAlbumArt(context: Context, albumId: Long): Uri? {
        if (albumId <= 0) return null
        val dir = File(context.cacheDir, "album_art")
        val f = File(dir, "album_$albumId.jpg")
        return if (f.exists()) Uri.fromFile(f) else null
    }
}
