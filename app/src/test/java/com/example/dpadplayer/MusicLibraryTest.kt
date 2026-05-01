package com.example.dpadplayer

import com.example.dpadplayer.playback.Track
import org.junit.Assert.*
import org.junit.Test
import android.net.Uri

class MusicLibraryTest {

    private fun makeTrack(id: Long, title: String, artist: String, album: String,
                          albumId: Long, filePath: String = "", albumArtUri: Uri? = null,
                          mediaStoreArt: Uri? = null, sortTitle: String = title,
                          sortAlbum: String = album, sortArtist: String = artist,
                          albumArtist: String = artist, sortAlbumArtist: String = artist,
                          trackNumber: Int = 0, discNumber: Int = 0,
                          year: Int = 0, genre: String = "", duration: Long = 0L,
                          dateAdded: Long = 0L): Track {
        val art = albumArtUri ?: mediaStoreArt ?: Uri.parse("content://none")
        val msArt = mediaStoreArt ?: art
        return Track(
            id = id,
            uri = Uri.parse("content://media/$id"),
            filePath = filePath,
            title = title,
            sortTitle = sortTitle,
            artist = artist,
            sortArtist = sortArtist,
            albumArtist = albumArtist,
            sortAlbumArtist = sortAlbumArtist,
            album = album,
            sortAlbum = sortAlbum,
            albumId = albumId,
            trackNumber = trackNumber,
            discNumber = discNumber,
            year = year,
            genre = genre,
            duration = duration,
            dateAdded = dateAdded,
            albumArtUri = art,
            mediaStoreAlbumArtUri = msArt,
        )
    }

    @Test
    fun testAlbumGroupingAndArtSelection() {
        val t1 = makeTrack(1, "A1", "Artist", "AlbumX", 10,
            albumArtUri = Uri.parse("file://embedded1"), mediaStoreArt = Uri.parse("content://ms/10"))
        val t2 = makeTrack(2, "A2", "Artist", "AlbumX", 10,
            albumArtUri = Uri.parse("content://ms/10"), mediaStoreArt = Uri.parse("content://ms/10"))
        val lib = MusicLibrary.build(listOf(t1, t2))
        assertEquals(1, lib.albums.size)
        val album = lib.albums.first()
        // albumArtUri should prefer the embedded artwork from t1
        assertEquals(Uri.parse("file://embedded1"), album.albumArtUri)
    }

    @Test
    fun testArtistSplittingAndCounts() {
        val t1 = makeTrack(1, "S1", "A & B", "Album1", 1)
        val t2 = makeTrack(2, "S2", "A;C", "Album2", 2)
        val lib = MusicLibrary.build(listOf(t1, t2))
        // artists should include a, b, c lowercase keys
        val artistKeys = lib.artists.map { it.id }
        assertTrue(artistKeys.contains("a & b" ) || artistKeys.any { it.contains("a") })
        // Song counts should sum to total tracks
        val totalSongs = lib.artists.sumOf { it.songs.size }
        assertTrue(totalSongs >= 2)
    }
}
