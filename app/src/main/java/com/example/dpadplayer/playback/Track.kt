package com.example.dpadplayer.playback

data class Track(
    val uri: String,
    val title: String,
    val artist: String,
    val albumArt: ByteArray? = null
)
