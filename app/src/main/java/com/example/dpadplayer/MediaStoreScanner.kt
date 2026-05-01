package com.example.dpadplayer

import android.content.ContentUris
import android.content.Context
// MediaMetadataRetriever was previously used; we now use jaudiotagger for robust tag parsing
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.provider.MediaStore
import com.example.dpadplayer.playback.Track
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import com.example.dpadplayer.db.AppDatabase

/**
 * Scans all MediaStore audio volumes (external + internal) and enriches each
 * track with full ID3/VorbisComment metadata via MediaMetadataRetriever.
 *
 * Strategy (mirrors Auxio's approach, adapted for Android MediaStore):
 *  1. Query MediaStore for file URIs + basic columns (fast bulk read).
 *  2. For each file, open MediaMetadataRetriever to read proper ID3 tags:
 *       title, artist, albumArtist, album, track#, disc#, year, genre, sortTitle, etc.
 *  3. Fall back to MediaStore column values when a tag is absent.
 *  4. Include is_pending files (freshly copied via adb push) via setIncludePending.
 *  5. Filter: duration >= 30 s OR duration IS NULL (pending files have NULL duration).
 */
object MediaStoreScanner {

    /** Optional coroutine scope for background tasks (set by caller). If null a default IO scope is used. */
    @Volatile
    var scope: CoroutineScope? = null

    private val defaultScope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.IO) }


    suspend fun loadTracks(context: Context, sortOrder: String = "title"): List<Track> {
        val raw = collectRawRows(context)
        // Load track cache to avoid re-enriching files that were previously processed.
        val db = try { AppDatabase.getInstance(context) } catch (_: Exception) { null }
        val cacheMap = db?.trackCacheDao()?.getAll()?.associateBy { it.trackId } ?: emptyMap()

        val tracks = raw.map { row ->
            val cached = cacheMap[row.id]
            if (cached != null) {
                // Build Track from cached metadata without opening the file
                val artUri = if (cached.albumArtPath.isNotBlank()) android.net.Uri.fromFile(File(cached.albumArtPath)) else Track.albumArtUri(cached.albumId)
                Track(
                    id = row.id,
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, row.id),
                    filePath = row.msData,
                    title = cached.title,
                    sortTitle = cached.sortTitle,
                    artist = cached.artist,
                    sortArtist = cached.sortArtist,
                    albumArtist = cached.albumArtist,
                    sortAlbumArtist = cached.sortAlbumArtist,
                    album = cached.album,
                    sortAlbum = cached.sortAlbum,
                    albumId = cached.albumId,
                    trackNumber = cached.trackNumber,
                    discNumber = cached.discNumber,
                    year = cached.year,
                    genre = cached.genre,
                    duration = cached.duration,
                    dateAdded = cached.dateAdded,
                    albumArtUri = artUri,
                    mediaStoreAlbumArtUri = Track.albumArtUri(row.msAlbumId),
                )
            } else {
                enrichWithRetriever(context, row)
            }
        }
        return applySortOrder(tracks, sortOrder)
    }

    // ── Step 1: MediaStore bulk query ─────────────────────────────────────────

    private data class RawRow(
        val id: Long,
        val uri: android.net.Uri,
        val msData: String,
        val msTitle: String,
        val msArtist: String,
        val msAlbum: String,
        val msAlbumId: Long,
        val msDuration: Long,
        val msDateAdded: Long,
        val isInternal: Boolean,
    )

    private suspend fun collectRawRows(context: Context): List<RawRow> {
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATE_ADDED,
            MediaStore.Audio.AudioColumns.IS_MUSIC,
        )

        val selection =
            "${MediaStore.Audio.AudioColumns.IS_MUSIC} != 0" +
            " AND (${MediaStore.Audio.AudioColumns.DURATION} >= 30000" +
            " OR ${MediaStore.Audio.AudioColumns.DURATION} IS NULL)"

        val baseUris = mutableListOf<Pair<android.net.Uri, Boolean>>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Query each external MediaStore volume explicitly (includes removable SD cards)
            try {
                val volumes = MediaStore.getExternalVolumeNames(context)
                for (vol in volumes) {
                    baseUris.add(MediaStore.Audio.Media.getContentUri(vol) to false)
                }
            } catch (_: Exception) {
                // Fall back if getExternalVolumeNames is unavailable or fails
                baseUris.add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to false)
            }
            // Always include internal volume as well
            baseUris.add(MediaStore.Audio.Media.INTERNAL_CONTENT_URI to true)
        } else {
            baseUris.add(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to false)
            baseUris.add(MediaStore.Audio.Media.INTERNAL_CONTENT_URI to true)
        }

        val rows = mutableListOf<RawRow>()
        val seenIds = mutableSetOf<Long>()

        for ((baseUri, isInternal) in baseUris) {
            val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.setIncludePending(baseUri) else baseUri
            try {
                context.contentResolver.query(
                    queryUri, projection, selection, null,
                    "${MediaStore.Audio.AudioColumns.TITLE} ASC"
                )?.use { c ->
                    val colId       = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val colData     = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
                    val colTitle    = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                    val colArtist   = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val colAlbum    = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                    val colAlbumId  = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                    val colDur      = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                    val colAdded    = c.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)

                    while (c.moveToNext()) {
                        val id = c.getLong(colId)
                        if (!seenIds.add(id)) continue
                        rows.add(RawRow(
                            id          = id,
                            uri         = ContentUris.withAppendedId(baseUri, id),
                            msData      = c.getString(colData)    ?: "",
                            msTitle     = c.getString(colTitle)   ?: "",
                            msArtist    = c.getString(colArtist)  ?: "",
                            msAlbum     = c.getString(colAlbum)   ?: "",
                            msAlbumId   = c.getLong(colAlbumId),
                            msDuration  = c.getLong(colDur),
                            msDateAdded = c.getLong(colAdded),
                            isInternal  = isInternal,
                        ))
                    }
                }
            } catch (_: Exception) { /* volume unavailable */ }
            // Stagger queries across volumes to avoid overwhelming the system
            try { delay(200) } catch (_: InterruptedException) { /* ignore */ }
        }
        return rows
    }

    // ── Step 2: enrich with jaudiotagger (ID3/Vorbis tags) ─────────────────

    private fun enrichWithRetriever(context: Context, row: RawRow): Track {
        var title       = row.msTitle.ifBlank { "Unknown" }
        var sortTitle   = title
        var artist      = row.msArtist.ifBlank { "Unknown Artist" }
        var sortArtist  = artist
        var albumArtist = artist
        var sortAlbumArtist = artist
        var album       = row.msAlbum.ifBlank { "Unknown Album" }
        var sortAlbum   = album
        var trackNum    = 0
        var discNum     = 0
        var year        = 0
        var genre       = ""
        var duration    = row.msDuration
        val mediaStoreAlbumArtUri = Track.albumArtUri(row.msAlbumId)

        // Heavy per-file parsing (jaudiotagger/AudioFileIO) is intentionally
        // avoided during the bulk MediaStore scan because opening every file
        // causes large memory and file-descriptor pressure on devices with
        // large libraries. Tags and embedded artwork should be loaded lazily
        // via loadEmbeddedArtwork() or a dedicated per-track enrichment call.

        return Track(
            id              = row.id,
            uri             = row.uri,
            filePath        = row.msData,
            title           = title,
            sortTitle       = sortTitle,
            artist          = artist,
            sortArtist      = sortArtist,
            albumArtist     = albumArtist,
            sortAlbumArtist = sortAlbumArtist,
            album           = album,
            sortAlbum       = sortAlbum,
            albumId         = row.msAlbumId,
            trackNumber     = trackNum,
            discNumber      = discNum,
            year            = year,
            genre           = genre,
            duration        = duration,
            dateAdded       = row.msDateAdded,
            albumArtUri     = mediaStoreAlbumArtUri,
            mediaStoreAlbumArtUri = mediaStoreAlbumArtUri,
        )
    }

    /**
     * Lazily extract and cache embedded album art for a single track.
     * Call this on a background thread when the track is first played or its
     * detail view is opened — NOT during the bulk library scan.
     */
    fun loadEmbeddedArtwork(context: Context, track: com.example.dpadplayer.playback.Track): Uri? {
        if (track.filePath.isBlank()) return null
        val cached = File(context.cacheDir, "album_art/track_${track.id}.jpg")
        if (cached.exists()) return Uri.fromFile(cached)
        return try {
            val f = File(track.filePath)
            if (!f.exists()) return null
            val audioFile = AudioFileIO.read(f)
            val artwork = audioFile.tag?.firstArtwork ?: return null
            val bytes = artwork.binaryData ?: return null
            persistEmbeddedArtwork(context, track.id, bytes)
        } catch (_: Exception) { null }
    }

    /**
     * Enrich a single Track by reading detailed tags and embedded artwork.
     * Returns a new Track instance with enriched fields, or the original if
     * enrichment fails.
     */
    fun enrichTrack(context: Context, track: com.example.dpadplayer.playback.Track): com.example.dpadplayer.playback.Track {
        if (track.filePath.isBlank()) return track
        try {
            val f = File(track.filePath)
            if (!f.exists()) return track
            val audioFile = AudioFileIO.read(f)
            val tag = audioFile.tag

            var title = track.title
            var sortTitle = track.sortTitle
            var artist = track.artist
            var sortArtist = track.sortArtist
            var albumArtist = track.albumArtist
            var sortAlbumArtist = track.sortAlbumArtist
            var album = track.album
            var sortAlbum = track.sortAlbum
            var trackNum = track.trackNumber
            var discNum = track.discNumber
            var year = track.year
            var genre = track.genre
            var duration = track.duration
            var albumArtUri = track.albumArtUri

            if (tag != null) {
                fun getStr(key: FieldKey) = tag.getFirst(key)?.trim()?.takeIf { it.isNotEmpty() }

                getStr(FieldKey.TITLE)?.let { title = it; sortTitle = it }
                getStr(FieldKey.TITLE_SORT)?.let { sortTitle = it }

                getStr(FieldKey.ARTIST)?.let { artist = it; sortArtist = it; albumArtist = it; sortAlbumArtist = it }
                getStr(FieldKey.ARTIST_SORT)?.let { sortArtist = it }

                getStr(FieldKey.ALBUM_ARTIST)?.let { albumArtist = it; sortAlbumArtist = it }
                getStr(FieldKey.ALBUM_ARTIST_SORT)?.let { sortAlbumArtist = it }

                getStr(FieldKey.ALBUM)?.let { album = it; sortAlbum = it }
                getStr(FieldKey.ALBUM_SORT)?.let { sortAlbum = it }

                getStr(FieldKey.GENRE)?.let { genre = it }
                getStr(FieldKey.YEAR)?.let { year = it.take(4).toIntOrNull() ?: year }

                getStr(FieldKey.TRACK)?.let { trackNum = it.substringBefore('/').trim().toIntOrNull() ?: trackNum }
                getStr(FieldKey.DISC_NO)?.let { discNum = it.substringBefore('/').trim().toIntOrNull() ?: discNum }

                val artwork = tag.firstArtwork
                if (artwork != null && artwork.binaryData != null) {
                    val art = persistEmbeddedArtwork(context, track.id, artwork.binaryData, track.albumId)
                    if (art != null) albumArtUri = art
                }
            }

            val header = audioFile.audioHeader
            if (header != null && header.trackLength > 0) {
                duration = header.trackLength * 1000L
            }

            val enriched = com.example.dpadplayer.playback.Track(
                id = track.id,
                uri = track.uri,
                filePath = track.filePath,
                title = title,
                sortTitle = sortTitle,
                artist = artist,
                sortArtist = sortArtist,
                albumArtist = albumArtist,
                sortAlbumArtist = sortAlbumArtist,
                album = album,
                sortAlbum = sortAlbum,
                albumId = track.albumId,
                trackNumber = trackNum,
                discNumber = discNum,
                year = year,
                genre = genre,
                duration = duration,
                dateAdded = track.dateAdded,
                albumArtUri = albumArtUri,
                mediaStoreAlbumArtUri = track.mediaStoreAlbumArtUri,
            )
            // Publish metadata event so UI/library can update incrementally
            try { ArtRepository.publishMetadata(MetadataEvent(enriched.id, enriched)) } catch (_: Exception) { }
            // Persist enriched track metadata to the track cache so we don't re-enrich on next start
            try {
                val db = AppDatabase.getInstance(context)
                val path = enriched.albumArtUri.path ?: ""
                val t = com.example.dpadplayer.db.TrackCacheEntity(
                    trackId = enriched.id,
                    title = enriched.title,
                    sortTitle = enriched.sortTitle,
                    artist = enriched.artist,
                    sortArtist = enriched.sortArtist,
                    albumArtist = enriched.albumArtist,
                    sortAlbumArtist = enriched.sortAlbumArtist,
                    album = enriched.album,
                    sortAlbum = enriched.sortAlbum,
                    albumId = enriched.albumId,
                    trackNumber = enriched.trackNumber,
                    discNumber = enriched.discNumber,
                    year = enriched.year,
                    genre = enriched.genre,
                    duration = enriched.duration,
                    dateAdded = enriched.dateAdded,
                    albumArtPath = path,
                )
                val writeScope = scope ?: defaultScope
                writeScope.launch(Dispatchers.IO) {
                    try { db.trackCacheDao().upsert(t) } catch (_: Exception) { }
                }
            } catch (_: Exception) { }
            return enriched
        } catch (_: Exception) {
            return track
        }
    }

    private fun persistEmbeddedArtwork(context: Context, trackId: Long, bytes: ByteArray, albumId: Long = 0L): Uri? {
        return try {
            val dir = File(context.cacheDir, "album_art")
            if (!dir.exists()) dir.mkdirs()
            val trackFile = File(dir, "track_$trackId.jpg")
            trackFile.writeBytes(bytes)
            // Also write album_<albumId>.jpg for quick album-level lookup (if albumId provided)
            var albumFileUri: Uri? = null
            if (albumId > 0) {
                try {
                    val albumFile = File(dir, "album_$albumId.jpg")
                    // overwrite with the latest track artwork (small cost but keeps album in sync)
                    albumFile.writeBytes(bytes)
                    albumFileUri = Uri.fromFile(albumFile)
                } catch (_: Exception) { /* ignore album write failures */ }
            }
            val trackUri = Uri.fromFile(trackFile)
            // Publish event with albumId if available
            ArtRepository.publishArtwork(ArtworkEvent(albumId, trackId, albumFileUri ?: trackUri))
            // Persist album cache entry in Room so data survives restarts
            try {
                val db = AppDatabase.getInstance(context)
                val albumName = /* best effort: try reading tag or fallback */ ""
                // We schedule a background write; don't block caller
                // write cache entry on a coroutine on IO
                try {
                    val writeScope = scope ?: defaultScope
                    writeScope.launch(Dispatchers.IO) {
                        try {
                            if (albumId > 0 && albumFileUri != null) {
                                val path = File(albumFileUri.path ?: "").absolutePath
                                db.albumCacheDao().upsert(
                                    com.example.dpadplayer.db.AlbumCacheEntity(
                                        albumId = albumId,
                                        name = albumName,
                                        artist = "",
                                        artPath = path,
                                    )
                                )
                            }
                        } catch (_: Exception) { /* ignore DB failures */ }
                    }
                } catch (_: Exception) { /* ignore */ }
            } catch (_: Exception) { /* ignore */ }
            trackUri
        } catch (_: Exception) {
            null
        }
    }

    // ── Step 3: sort ──────────────────────────────────────────────────────────

    private fun applySortOrder(tracks: List<Track>, sortOrder: String): List<Track> {
        return when (sortOrder) {
            "artist"     -> tracks.sortedWith(compareBy(
                { it.sortArtist.lowercase() }, { it.sortAlbum.lowercase() },
                { it.discNumber }, { it.trackNumber }, { it.sortTitle.lowercase() }))
            "album"      -> tracks.sortedWith(compareBy(
                { it.sortAlbum.lowercase() }, { it.discNumber },
                { it.trackNumber }, { it.sortTitle.lowercase() }))
            "date_added" -> tracks.sortedByDescending { it.dateAdded }
            "year"       -> tracks.sortedWith(compareByDescending<Track> { it.year }
                .thenBy { it.sortAlbum.lowercase() }
                .thenBy { it.discNumber }.thenBy { it.trackNumber })
            else         -> tracks.sortedBy { it.sortTitle.lowercase() }  // "title"
        }
    }
}
