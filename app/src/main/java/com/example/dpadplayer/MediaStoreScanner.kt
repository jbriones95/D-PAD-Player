package com.example.dpadplayer

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.dpadplayer.playback.Track

object MediaStoreScanner {

    fun loadTracks(context: Context, sortOrder: String = "title"): List<Track> {
        val tracks = mutableListOf<Track>()

        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
        )

        val selection = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = 1 " +
                "AND ${MediaStore.Audio.AudioColumns.TITLE} != ''"

        val orderClause = when (sortOrder) {
            "artist"     -> "${MediaStore.Audio.AudioColumns.ARTIST} ASC, ${MediaStore.Audio.AudioColumns.TITLE} ASC"
            "album"      -> "${MediaStore.Audio.AudioColumns.ALBUM} ASC, ${MediaStore.Audio.AudioColumns.TITLE} ASC"
            "date_added" -> "${MediaStore.Audio.AudioColumns.DATE_ADDED} DESC"
            else         -> "${MediaStore.Audio.AudioColumns.TITLE} ASC"
        }

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
                val id      = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdCol)
                val uri     = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val artUri  = Track.albumArtUri(albumId)

                tracks.add(Track(
                    id        = id,
                    uri       = uri,
                    title     = cursor.getString(titleCol) ?: "Unknown",
                    artist    = cursor.getString(artistCol) ?: "Unknown",
                    album     = cursor.getString(albumCol) ?: "Unknown",
                    albumId   = albumId,
                    duration  = cursor.getLong(durCol),
                    albumArtUri = artUri,
                ))
            }
        }

        return tracks
    }
}
