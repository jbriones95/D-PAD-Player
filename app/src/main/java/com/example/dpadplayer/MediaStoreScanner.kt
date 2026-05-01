package com.example.dpadplayer

import android.content.ContentUris
import android.content.Context
// MediaMetadataRetriever was previously used; we now use jaudiotagger for robust tag parsing
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.dpadplayer.playback.Track
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

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

    fun loadTracks(context: Context, sortOrder: String = "title"): List<Track> {
        val raw = collectRawRows(context)
        val tracks = raw.map { row -> enrichWithRetriever(context, row) }
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

    private fun collectRawRows(context: Context): List<RawRow> {
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

        val baseUris = listOf(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to false,
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI to true,
        )

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

        if (row.msData.isNotEmpty()) {
            try {
                val f = File(row.msData)
                if (f.exists()) {
                    val audioFile = AudioFileIO.read(f)
                    val tag = audioFile.tag

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
                        
                        getStr(FieldKey.YEAR)?.let {
                            // Can be full date "2024-01-01" or year "2024"
                            year = it.take(4).toIntOrNull() ?: 0
                        }

                        // Track number
                        getStr(FieldKey.TRACK)?.let {
                            trackNum = it.substringBefore('/').trim().toIntOrNull() ?: 0
                        }

                        // Disc number
                        getStr(FieldKey.DISC_NO)?.let {
                            discNum = it.substringBefore('/').trim().toIntOrNull() ?: 0
                        }

                        // NOTE: Embedded artwork is extracted lazily (see loadEmbeddedArtwork)
                        // to avoid OOM/crash when scanning large libraries.
                    }

                    // Duration from header
                    val header = audioFile.audioHeader
                    if (header != null && header.trackLength > 0) {
                        // trackLength is in seconds, convert to ms
                        duration = header.trackLength * 1000L
                    }
                }
            } catch (e: Exception) {
                // Ignore jaudiotagger errors (corrupt tags, unsupported formats)
                // We fallback to the MediaStore row defaults
            }
        }

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

    private fun persistEmbeddedArtwork(context: Context, trackId: Long, bytes: ByteArray): Uri? {
        return try {
            val dir = File(context.cacheDir, "album_art")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "track_$trackId.jpg")
            file.writeBytes(bytes)
            Uri.fromFile(file)
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
