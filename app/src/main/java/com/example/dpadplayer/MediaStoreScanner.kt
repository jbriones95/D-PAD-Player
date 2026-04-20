package com.example.dpadplayer

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.example.dpadplayer.playback.Track

object MediaStoreScanner {

    /**
     * Query every MediaStore audio URI that Android exposes:
     *   - External storage (sdcard, emulated storage, adopted storage)
     *   - Internal storage (system sounds, pre-installed audio)
     *
     * We intentionally do NOT filter by IS_MUSIC so that tracks stored in
     * Podcasts/, Audiobooks/, Recordings/, Ringtones/, etc. are all included.
     * The only filter is a minimum duration (≥ 30 s) to skip UI sound effects.
     *
     * Results from both URIs are merged and de-duplicated by track ID.
     */
    fun loadTracks(context: Context, sortOrder: String = "title"): List<Track> {
        val orderClause = when (sortOrder) {
            "artist"     -> "${MediaStore.Audio.AudioColumns.ARTIST} ASC, ${MediaStore.Audio.AudioColumns.TITLE} ASC"
            "album"      -> "${MediaStore.Audio.AudioColumns.ALBUM} ASC, ${MediaStore.Audio.AudioColumns.TITLE} ASC"
            "date_added" -> "${MediaStore.Audio.AudioColumns.DATE_ADDED} DESC"
            else         -> "${MediaStore.Audio.AudioColumns.TITLE} ASC"
        }

        val uris = listOf(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
        )

        val tracks = mutableListOf<Track>()
        val seenIds = mutableSetOf<Long>()

        for (baseUri in uris) {
            queryUri(context, baseUri, orderClause, tracks, seenIds)
        }

        // Re-sort merged list (internal + external were each sorted independently)
        val comparator: Comparator<Track> = when (sortOrder) {
            "artist"     -> compareBy({ it.artist.lowercase() }, { it.title.lowercase() })
            "album"      -> compareBy({ it.album.lowercase() }, { it.title.lowercase() })
            "date_added" -> compareByDescending { it.id } // id proxy; DATE_ADDED not stored in Track
            else         -> compareBy { it.title.lowercase() }
        }
        tracks.sortWith(comparator)

        return tracks
    }

    private fun queryUri(
        context: Context,
        baseUri: android.net.Uri,
        orderClause: String,
        out: MutableList<Track>,
        seenIds: MutableSet<Long>,
    ) {
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
        )

        // Accept any audio with a title and duration >= 30 seconds.
        // No IS_MUSIC restriction — this captures podcasts, audiobooks,
        // recordings, ringtones, downloaded audio, etc.
        // We also allow NULL duration so files still being scanned aren't silently skipped.
        val selection =
            "(${MediaStore.Audio.AudioColumns.DURATION} >= 30000 " +
            " OR ${MediaStore.Audio.AudioColumns.DURATION} IS NULL)" +
            " AND ${MediaStore.Audio.AudioColumns.TITLE} != ''"

        // On Android 10+ include is_pending files (e.g. freshly adb-pushed tracks
        // that haven't been fully scanned yet).
        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.setIncludePending(baseUri)
        } else {
            baseUri
        }

        try {
            context.contentResolver.query(
                queryUri,
                projection,
                selection,
                null,
                orderClause
            )?.use { cursor ->
                val idCol      = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val titleCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                val artistCol  = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val albumCol   = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                val durCol     = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    if (!seenIds.add(id)) continue   // skip duplicate

                    val albumId = cursor.getLong(albumIdCol)
                    val uri     = ContentUris.withAppendedId(baseUri, id)
                    val artUri  = Track.albumArtUri(albumId)

                    out.add(Track(
                        id          = id,
                        uri         = uri,
                        title       = cursor.getString(titleCol) ?: "Unknown",
                        artist      = cursor.getString(artistCol) ?: "Unknown",
                        album       = cursor.getString(albumCol)  ?: "Unknown",
                        albumId     = albumId,
                        duration    = cursor.getLong(durCol),
                        albumArtUri = artUri,
                    ))
                }
            }
        } catch (_: Exception) {
            // Some URIs may not be available on all devices; silently skip.
        }
    }
}
