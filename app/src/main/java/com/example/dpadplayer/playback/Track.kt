package com.example.dpadplayer.playback

import android.net.Uri

data class Track(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,          // ms
    val albumArtUri: Uri,
) {
    companion object {
        fun albumArtUri(albumId: Long): Uri =
            Uri.parse("content://media/external/audio/albumart/$albumId")
    }
}
