package com.example.dpadplayer.playback

import android.net.Uri

/**
 * Represents a single audio file with full ID3/VorbisComment tag metadata.
 *
 * Fields mirror what Auxio's ParsedTags captures, extracted here via
 * MediaMetadataRetriever + MediaStore columns.
 */
data class Track(
    // ── Identity ─────────────────────────────────────────────────────────────
    val id: Long,
    val uri: Uri,

    // ── Core display ─────────────────────────────────────────────────────────
    val title: String,
    val sortTitle: String,          // TSOT / TitleSort — used for sorting, falls back to title

    // ── Artists ──────────────────────────────────────────────────────────────
    val artist: String,             // TPE1 / ARTIST (track artist)
    val sortArtist: String,         // TSOP / ArtistSort
    val albumArtist: String,        // TPE2 / ALBUMARTIST (for album grouping)
    val sortAlbumArtist: String,    // TSO2 / AlbumArtistSort

    // ── Album ─────────────────────────────────────────────────────────────────
    val album: String,              // TALB / ALBUM
    val sortAlbum: String,          // TSOA / AlbumSort
    val albumId: Long,              // MediaStore album ID (for art URI)

    // ── Track / disc numbering ────────────────────────────────────────────────
    val trackNumber: Int,           // TRCK / TRACKNUMBER (0 = unknown)
    val discNumber: Int,            // TPOS / DISCNUMBER  (0 = unknown)

    // ── Date ──────────────────────────────────────────────────────────────────
    val year: Int,                  // TDRC / TYER / DATE (0 = unknown)

    // ── Genre ─────────────────────────────────────────────────────────────────
    val genre: String,              // TCON / GENRE

    // ── Playback ──────────────────────────────────────────────────────────────
    val duration: Long,             // ms
    val dateAdded: Long,            // epoch seconds from MediaStore DATE_ADDED

    // ── Art ───────────────────────────────────────────────────────────────────
    val albumArtUri: Uri,
    val mediaStoreAlbumArtUri: Uri,
) {
    companion object {
        fun albumArtUri(albumId: Long): Uri =
            Uri.parse("content://media/external/audio/albumart/$albumId")
    }
}
