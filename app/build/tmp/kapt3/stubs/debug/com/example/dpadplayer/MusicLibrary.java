package com.example.dpadplayer;

/**
 * Builds Albums, Artists, and Genres from a flat list of Tracks.
 * Grouping logic inspired by Auxio's MusicGraph:
 * - Albums keyed by (sortAlbum + albumArtist) — same name by different artists = separate albums
 * - Artists keyed by albumArtist (for album ownership) and trackArtist (for song membership)
 * - Genres keyed by lowercased genre name (multi-genre: split on ';' or '/')
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0014B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0014\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nJ\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\n2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002J*\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\n2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\n2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\nH\u0002J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\n2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nH\u0002J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\n2\u0006\u0010\u0013\u001a\u00020\u0004H\u0002\u00a8\u0006\u0015"}, d2 = {"Lcom/example/dpadplayer/MusicLibrary;", "", "()V", "albumKey", "", "t", "Lcom/example/dpadplayer/playback/Track;", "build", "Lcom/example/dpadplayer/MusicLibrary$Library;", "tracks", "", "buildAlbums", "Lcom/example/dpadplayer/Album;", "buildArtists", "Lcom/example/dpadplayer/Artist;", "albums", "buildGenres", "Lcom/example/dpadplayer/Genre;", "splitArtists", "raw", "Library", "app_debug"})
public final class MusicLibrary {
    @org.jetbrains.annotations.NotNull
    public static final com.example.dpadplayer.MusicLibrary INSTANCE = null;
    
    private MusicLibrary() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.dpadplayer.MusicLibrary.Library build(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.dpadplayer.playback.Track> tracks) {
        return null;
    }
    
    private final java.util.List<com.example.dpadplayer.Album> buildAlbums(java.util.List<com.example.dpadplayer.playback.Track> tracks) {
        return null;
    }
    
    private final java.lang.String albumKey(com.example.dpadplayer.playback.Track t) {
        return null;
    }
    
    private final java.util.List<com.example.dpadplayer.Artist> buildArtists(java.util.List<com.example.dpadplayer.playback.Track> tracks, java.util.List<com.example.dpadplayer.Album> albums) {
        return null;
    }
    
    private final java.util.List<java.lang.String> splitArtists(java.lang.String raw) {
        return null;
    }
    
    private final java.util.List<com.example.dpadplayer.Genre> buildGenres(java.util.List<com.example.dpadplayer.playback.Track> tracks) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\u0002\u0010\u000bJ\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0003H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0003JI\u0010\u0015\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\r\u00a8\u0006\u001d"}, d2 = {"Lcom/example/dpadplayer/MusicLibrary$Library;", "", "tracks", "", "Lcom/example/dpadplayer/playback/Track;", "albums", "Lcom/example/dpadplayer/Album;", "artists", "Lcom/example/dpadplayer/Artist;", "genres", "Lcom/example/dpadplayer/Genre;", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;)V", "getAlbums", "()Ljava/util/List;", "getArtists", "getGenres", "getTracks", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class Library {
        @org.jetbrains.annotations.NotNull
        private final java.util.List<com.example.dpadplayer.playback.Track> tracks = null;
        @org.jetbrains.annotations.NotNull
        private final java.util.List<com.example.dpadplayer.Album> albums = null;
        @org.jetbrains.annotations.NotNull
        private final java.util.List<com.example.dpadplayer.Artist> artists = null;
        @org.jetbrains.annotations.NotNull
        private final java.util.List<com.example.dpadplayer.Genre> genres = null;
        
        public Library(@org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.playback.Track> tracks, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Album> albums, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Artist> artists, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Genre> genres) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.playback.Track> getTracks() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Album> getAlbums() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Artist> getArtists() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Genre> getGenres() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.playback.Track> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Album> component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Artist> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.List<com.example.dpadplayer.Genre> component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.example.dpadplayer.MusicLibrary.Library copy(@org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.playback.Track> tracks, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Album> albums, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Artist> artists, @org.jetbrains.annotations.NotNull
        java.util.List<com.example.dpadplayer.Genre> genres) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
}