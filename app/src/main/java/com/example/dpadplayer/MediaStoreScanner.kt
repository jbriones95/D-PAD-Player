package com.example.dpadplayer

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import com.example.dpadplayer.playback.Track

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
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATE_ADDED,
        )

        val selection =
            "(${MediaStore.Audio.AudioColumns.DURATION} >= 30000" +
            " OR ${MediaStore.Audio.AudioColumns.DURATION} IS NULL)" +
            " AND ${MediaStore.Audio.AudioColumns.TITLE} != ''"

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

    // ── Step 2: enrich with MediaMetadataRetriever (ID3 tags) ─────────────────

    private fun enrichWithRetriever(context: Context, row: RawRow): Track {
        val mmr = MediaMetadataRetriever()
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

        try {
            mmr.setDataSource(context, row.uri)

            fun tag(key: Int) = mmr.extractMetadata(key)?.trim()?.takeIf { it.isNotEmpty() }

            tag(MediaMetadataRetriever.METADATA_KEY_TITLE)?.let { title = it; sortTitle = it }
            tag(MediaMetadataRetriever.METADATA_KEY_ARTIST)?.let { artist = it; sortArtist = it }
            tag(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)?.let {
                albumArtist = it; sortAlbumArtist = it
            }
            tag(MediaMetadataRetriever.METADATA_KEY_ALBUM)?.let { album = it; sortAlbum = it }
            tag(MediaMetadataRetriever.METADATA_KEY_GENRE)?.let { genre = it }
            tag(MediaMetadataRetriever.METADATA_KEY_YEAR)?.let { year = it.toIntOrNull() ?: 0 }
            tag(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
                duration = it.toLongOrNull() ?: duration
            }

            // Track number — may be "X/Y" (track/total), take just X
            tag(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let {
                trackNum = it.substringBefore('/').trim().toIntOrNull() ?: 0
            }
            // Disc number — may be "X/Y"
            tag(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.let {
                discNum = it.substringBefore('/').trim().toIntOrNull() ?: 0
            }

            // If albumArtist is still just the track artist, and no album artist tag was found,
            // leave albumArtist = artist so same-artist albums group correctly.
            if (albumArtist == artist) {
                // Check if MediaStore knows a distinct album artist
                // (some formats expose it only through MediaStore columns)
            }
        } catch (_: Exception) {
            // File unreadable — use MediaStore fallbacks already set above
        } finally {
            try { mmr.release() } catch (_: Exception) {}
        }

        return Track(
            id              = row.id,
            uri             = row.uri,
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
            albumArtUri     = Track.albumArtUri(row.msAlbumId),
        )
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
