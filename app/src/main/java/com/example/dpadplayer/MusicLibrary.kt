package com.example.dpadplayer

import android.net.Uri
import com.example.dpadplayer.playback.Track

// ── Album ─────────────────────────────────────────────────────────────────────

data class Album(
    val id: String,              // key: sortAlbum.lowercase() + "|" + albumArtist.lowercase()
    val name: String,
    val sortName: String,
    val artist: String,          // album artist (TPE2/ALBUMARTIST or track artist)
    val year: Int,               // earliest year from songs (0 = unknown)
    val songs: List<Track>,      // sorted by disc, then track, then title
    val albumArtUri: Uri,        // art from first song that has art
) {
    val durationMs: Long get() = songs.sumOf { it.duration }
    val songCount: Int   get() = songs.size
}

// ── Artist ────────────────────────────────────────────────────────────────────

data class Artist(
    val id: String,              // key: sortArtist.lowercase()
    val name: String,
    val sortName: String,
    val albums: List<Album>,     // albums where this is the album artist
    val songs: List<Track>,      // all songs by this artist (incl. features)
) {
    val durationMs: Long get() = songs.sumOf { it.duration }
    val albumCount: Int  get() = albums.size
}

// ── Genre ─────────────────────────────────────────────────────────────────────

data class Genre(
    val id: String,              // key: genre.lowercase()
    val name: String,
    val songs: List<Track>,
) {
    val durationMs: Long get() = songs.sumOf { it.duration }
    val songCount: Int   get() = songs.size
}

// ── MusicLibrary ──────────────────────────────────────────────────────────────

/**
 * Builds Albums, Artists, and Genres from a flat list of Tracks.
 * Grouping logic inspired by Auxio's MusicGraph:
 *  - Albums keyed by (sortAlbum + albumArtist) — same name by different artists = separate albums
 *  - Artists keyed by albumArtist (for album ownership) and trackArtist (for song membership)
 *  - Genres keyed by lowercased genre name (multi-genre: split on ';' or '/')
 */
object MusicLibrary {

    data class Library(
        val tracks: List<Track>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val genres: List<Genre>,
    )

    fun build(tracks: List<Track>): Library {
        val albums  = buildAlbums(tracks)
        val artists = buildArtists(tracks, albums)
        val genres  = buildGenres(tracks)
        return Library(tracks, albums, artists, genres)
    }

    // ── Albums ────────────────────────────────────────────────────────────────

    private fun buildAlbums(tracks: List<Track>): List<Album> {
        // Group by (album name + album artist) — case-insensitive
        val groups = LinkedHashMap<String, MutableList<Track>>()
        for (t in tracks) {
            val key = albumKey(t)
            groups.getOrPut(key) { mutableListOf() }.add(t)
        }

        return groups.entries.map { (key, songs) ->
            val representative = songs.first()
            val sorted = songs.sortedWith(
                compareBy({ it.discNumber.let { d -> if (d == 0) Int.MAX_VALUE else d } },
                          { it.trackNumber.let { n -> if (n == 0) Int.MAX_VALUE else n } },
                          { it.sortTitle.lowercase() }))
            val art = sorted.firstOrNull { it.albumArtUri.toString().isNotBlank() }?.albumArtUri
                ?: representative.mediaStoreAlbumArtUri
            Album(
                id          = key,
                name        = representative.album,
                sortName    = representative.sortAlbum,
                artist      = representative.albumArtist,
                year        = songs.mapNotNull { it.year.takeIf { y -> y > 0 } }.minOrNull() ?: 0,
                songs       = sorted,
                albumArtUri = art,
            )
        }.sortedBy { it.sortName.lowercase() }
    }

    private fun albumKey(t: Track) =
        "${t.sortAlbum.lowercase()}|${t.albumArtist.lowercase()}"

    // ── Artists ───────────────────────────────────────────────────────────────

    private fun buildArtists(tracks: List<Track>, albums: List<Album>): List<Artist> {
        // Map album artist → albums
        val albumsByArtistKey = LinkedHashMap<String, MutableList<Album>>()
        for (album in albums) {
            val key = album.artist.lowercase()
            albumsByArtistKey.getOrPut(key) { mutableListOf() }.add(album)
        }

        // Map artist → songs (by track artist — catches features)
        val songsByArtistKey = LinkedHashMap<String, MutableList<Track>>()
        for (t in tracks) {
            // split multi-artist fields like "A; B" or "A / B"
            val artistNames = splitArtists(t.artist)
            for (name in artistNames) {
                songsByArtistKey.getOrPut(name.lowercase()) { mutableListOf() }.add(t)
            }
        }

        // Union of all artist keys
        val allKeys = (albumsByArtistKey.keys + songsByArtistKey.keys).toSet()

        return allKeys.map { key ->
            val albumList = albumsByArtistKey[key] ?: emptyList()
            val songList  = songsByArtistKey[key]  ?: emptyList()
            val displayName = albumList.firstOrNull()?.artist
                ?: songList.firstOrNull()?.artist
                ?: key
            val sortName = albumList.firstOrNull()?.artist
                ?: songList.firstOrNull()?.sortArtist
                ?: key
            Artist(
                id       = key,
                name     = displayName,
                sortName = sortName,
                albums   = albumList.sortedByDescending { it.year },
                songs    = songList.sortedBy { it.sortTitle.lowercase() },
            )
        }.sortedBy { it.sortName.lowercase() }
    }

    private fun splitArtists(raw: String): List<String> {
        // Common separators: " / ", "; ", " feat. ", " & "
        return raw.split(Regex(";|/|feat\\.|&")).map { it.trim() }.filter { it.isNotEmpty() }
            .ifEmpty { listOf(raw) }
    }

    // ── Genres ────────────────────────────────────────────────────────────────

    private fun buildGenres(tracks: List<Track>): List<Genre> {
        val groups = LinkedHashMap<String, MutableList<Track>>()
        for (t in tracks) {
            val names = if (t.genre.isBlank()) listOf("Unknown") else
                t.genre.split(Regex(";|/|,")).map { it.trim() }.filter { it.isNotEmpty() }
            for (name in names) {
                groups.getOrPut(name.lowercase()) { mutableListOf() }.add(t)
            }
        }
        return groups.entries.map { (key, songs) ->
            Genre(
                id    = key,
                name  = songs.first().genre.split(Regex(";|/|,")).firstOrNull()?.trim() ?: key,
                songs = songs.sortedBy { it.sortTitle.lowercase() },
            )
        }.sortedBy { it.name.lowercase() }
    }
}
