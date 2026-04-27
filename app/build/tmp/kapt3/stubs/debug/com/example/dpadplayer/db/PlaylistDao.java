package com.example.dpadplayer.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\fH\'J\u001b\u0010\u000e\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000f\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00110\r0\f2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00110\r2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0013\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u001f\u0010\u0014\u001a\u00020\u00032\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\rH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016J!\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0019J!\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u001cH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001d\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001e"}, d2 = {"Lcom/example/dpadplayer/db/PlaylistDao;", "", "clearPlaylist", "", "playlistId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePlaylist", "playlist", "Lcom/example/dpadplayer/db/PlaylistEntity;", "(Lcom/example/dpadplayer/db/PlaylistEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllPlaylists", "Lkotlinx/coroutines/flow/Flow;", "", "getPlaylist", "id", "getSongsForPlaylist", "Lcom/example/dpadplayer/db/PlaylistSongEntity;", "getSongsForPlaylistOnce", "insertPlaylist", "insertSongs", "songs", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeSongFromPlaylist", "trackId", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "renamePlaylist", "name", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao
public abstract interface PlaylistDao {
    
    @androidx.room.Query(value = "SELECT * FROM playlists ORDER BY name COLLATE NOCASE ASC")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.dpadplayer.db.PlaylistEntity>> getAllPlaylists();
    
    @androidx.room.Query(value = "SELECT * FROM playlists WHERE id = :id")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPlaylist(long id, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.dpadplayer.db.PlaylistEntity> $completion);
    
    @androidx.room.Insert
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insertPlaylist(@org.jetbrains.annotations.NotNull
    com.example.dpadplayer.db.PlaylistEntity playlist, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "UPDATE playlists SET name = :name WHERE id = :id")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object renamePlaylist(long id, @org.jetbrains.annotations.NotNull
    java.lang.String name, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object deletePlaylist(@org.jetbrains.annotations.NotNull
    com.example.dpadplayer.db.PlaylistEntity playlist, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position ASC")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.example.dpadplayer.db.PlaylistSongEntity>> getSongsForPlaylist(long playlistId);
    
    @androidx.room.Query(value = "SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY position ASC")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getSongsForPlaylistOnce(long playlistId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.example.dpadplayer.db.PlaylistSongEntity>> $completion);
    
    @androidx.room.Insert
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insertSongs(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.dpadplayer.db.PlaylistSongEntity> songs, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object clearPlaylist(long playlistId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId AND rowId IN (SELECT rowId FROM playlist_songs WHERE playlistId = :playlistId AND trackId = :trackId LIMIT 1)")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object removeSongFromPlaylist(long playlistId, long trackId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}