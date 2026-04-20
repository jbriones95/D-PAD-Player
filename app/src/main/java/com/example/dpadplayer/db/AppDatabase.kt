package com.example.dpadplayer.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Entities ──────────────────────────────────────────────────────────────────

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
)

/**
 * Each row represents one song slot in a playlist.
 * trackId is the MediaStore audio _ID.
 * position is the 0-based order within the playlist.
 * Using a separate row per slot allows duplicates and easy reordering.
 */
@Entity(
    tableName = "playlist_songs",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("playlistId")],
)
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Long = 0,
    val playlistId: Long,
    val trackId: Long,   // MediaStore Audio._ID
    val position: Int,
)

// ── DAO ───────────────────────────────────────────────────────────────────────

@Dao
interface PlaylistDao {

    // ── Playlists ─────────────────────────────────────────────────────────────

    @Query("SELECT * FROM playlists ORDER BY name COLLATE NOCASE ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylist(id: Long): PlaylistEntity?

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    suspend fun renamePlaylist(id: Long, name: String)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    // ── Songs ─────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position ASC")
    fun getSongsForPlaylist(playlistId: Long): Flow<List<PlaylistSongEntity>>

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position ASC")
    suspend fun getSongsForPlaylistOnce(playlistId: Long): List<PlaylistSongEntity>

    @Insert
    suspend fun insertSongs(songs: List<PlaylistSongEntity>)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId AND rowId IN (SELECT rowId FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId LIMIT 1)")
    suspend fun removeSongFromPlaylist(playlistId: Long, trackId: Long)
}

// ── Database ──────────────────────────────────────────────────────────────────

@Database(entities = [PlaylistEntity::class, PlaylistSongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dpad_player.db"
                ).build().also { INSTANCE = it }
            }
    }
}
