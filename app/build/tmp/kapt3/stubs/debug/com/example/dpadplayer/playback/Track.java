package com.example.dpadplayer.playback;

/**
 * Represents a single audio file with full ID3/VorbisComment tag metadata.
 *
 * Fields mirror what Auxio's ParsedTags captures, extracted here via
 * MediaMetadataRetriever + MediaStore columns.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b1\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u0000 G2\u00020\u0001:\u0001GB\u0095\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\u0007\u0012\u0006\u0010\r\u001a\u00020\u0007\u0012\u0006\u0010\u000e\u001a\u00020\u0007\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u0012\u0006\u0010\u0012\u001a\u00020\u0011\u0012\u0006\u0010\u0013\u001a\u00020\u0011\u0012\u0006\u0010\u0014\u001a\u00020\u0007\u0012\u0006\u0010\u0015\u001a\u00020\u0003\u0012\u0006\u0010\u0016\u001a\u00020\u0003\u0012\u0006\u0010\u0017\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0018J\t\u0010/\u001a\u00020\u0003H\u00c6\u0003J\t\u00100\u001a\u00020\u0007H\u00c6\u0003J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\t\u00102\u001a\u00020\u0011H\u00c6\u0003J\t\u00103\u001a\u00020\u0011H\u00c6\u0003J\t\u00104\u001a\u00020\u0011H\u00c6\u0003J\t\u00105\u001a\u00020\u0007H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\u0005H\u00c6\u0003J\t\u00109\u001a\u00020\u0005H\u00c6\u0003J\t\u0010:\u001a\u00020\u0007H\u00c6\u0003J\t\u0010;\u001a\u00020\u0007H\u00c6\u0003J\t\u0010<\u001a\u00020\u0007H\u00c6\u0003J\t\u0010=\u001a\u00020\u0007H\u00c6\u0003J\t\u0010>\u001a\u00020\u0007H\u00c6\u0003J\t\u0010?\u001a\u00020\u0007H\u00c6\u0003J\t\u0010@\u001a\u00020\u0007H\u00c6\u0003J\u00bd\u0001\u0010A\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u00072\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u00112\b\b\u0002\u0010\u0014\u001a\u00020\u00072\b\b\u0002\u0010\u0015\u001a\u00020\u00032\b\b\u0002\u0010\u0016\u001a\u00020\u00032\b\b\u0002\u0010\u0017\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010B\u001a\u00020C2\b\u0010D\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010E\u001a\u00020\u0011H\u00d6\u0001J\t\u0010F\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0017\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001aR\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001aR\u0011\u0010\u0016\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001fR\u0011\u0010\u0012\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u0015\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001fR\u0011\u0010\u0014\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001aR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001fR\u0011\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001aR\u0011\u0010\f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001aR\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001aR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001aR\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010#R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001cR\u0011\u0010\u0013\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010#\u00a8\u0006H"}, d2 = {"Lcom/example/dpadplayer/playback/Track;", "", "id", "", "uri", "Landroid/net/Uri;", "title", "", "sortTitle", "artist", "sortArtist", "albumArtist", "sortAlbumArtist", "album", "sortAlbum", "albumId", "trackNumber", "", "discNumber", "year", "genre", "duration", "dateAdded", "albumArtUri", "(JLandroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JIIILjava/lang/String;JJLandroid/net/Uri;)V", "getAlbum", "()Ljava/lang/String;", "getAlbumArtUri", "()Landroid/net/Uri;", "getAlbumArtist", "getAlbumId", "()J", "getArtist", "getDateAdded", "getDiscNumber", "()I", "getDuration", "getGenre", "getId", "getSortAlbum", "getSortAlbumArtist", "getSortArtist", "getSortTitle", "getTitle", "getTrackNumber", "getUri", "getYear", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "Companion", "app_debug"})
public final class Track {
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull
    private final android.net.Uri uri = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String title = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sortTitle = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String artist = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sortArtist = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String albumArtist = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sortAlbumArtist = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String album = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String sortAlbum = null;
    private final long albumId = 0L;
    private final int trackNumber = 0;
    private final int discNumber = 0;
    private final int year = 0;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String genre = null;
    private final long duration = 0L;
    private final long dateAdded = 0L;
    @org.jetbrains.annotations.NotNull
    private final android.net.Uri albumArtUri = null;
    @org.jetbrains.annotations.NotNull
    public static final com.example.dpadplayer.playback.Track.Companion Companion = null;
    
    public Track(long id, @org.jetbrains.annotations.NotNull
    android.net.Uri uri, @org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String sortTitle, @org.jetbrains.annotations.NotNull
    java.lang.String artist, @org.jetbrains.annotations.NotNull
    java.lang.String sortArtist, @org.jetbrains.annotations.NotNull
    java.lang.String albumArtist, @org.jetbrains.annotations.NotNull
    java.lang.String sortAlbumArtist, @org.jetbrains.annotations.NotNull
    java.lang.String album, @org.jetbrains.annotations.NotNull
    java.lang.String sortAlbum, long albumId, int trackNumber, int discNumber, int year, @org.jetbrains.annotations.NotNull
    java.lang.String genre, long duration, long dateAdded, @org.jetbrains.annotations.NotNull
    android.net.Uri albumArtUri) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.net.Uri getUri() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSortTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getArtist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSortArtist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAlbumArtist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSortAlbumArtist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAlbum() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSortAlbum() {
        return null;
    }
    
    public final long getAlbumId() {
        return 0L;
    }
    
    public final int getTrackNumber() {
        return 0;
    }
    
    public final int getDiscNumber() {
        return 0;
    }
    
    public final int getYear() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getGenre() {
        return null;
    }
    
    public final long getDuration() {
        return 0L;
    }
    
    public final long getDateAdded() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.net.Uri getAlbumArtUri() {
        return null;
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component10() {
        return null;
    }
    
    public final long component11() {
        return 0L;
    }
    
    public final int component12() {
        return 0;
    }
    
    public final int component13() {
        return 0;
    }
    
    public final int component14() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component15() {
        return null;
    }
    
    public final long component16() {
        return 0L;
    }
    
    public final long component17() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.net.Uri component18() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final android.net.Uri component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.dpadplayer.playback.Track copy(long id, @org.jetbrains.annotations.NotNull
    android.net.Uri uri, @org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String sortTitle, @org.jetbrains.annotations.NotNull
    java.lang.String artist, @org.jetbrains.annotations.NotNull
    java.lang.String sortArtist, @org.jetbrains.annotations.NotNull
    java.lang.String albumArtist, @org.jetbrains.annotations.NotNull
    java.lang.String sortAlbumArtist, @org.jetbrains.annotations.NotNull
    java.lang.String album, @org.jetbrains.annotations.NotNull
    java.lang.String sortAlbum, long albumId, int trackNumber, int discNumber, int year, @org.jetbrains.annotations.NotNull
    java.lang.String genre, long duration, long dateAdded, @org.jetbrains.annotations.NotNull
    android.net.Uri albumArtUri) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/dpadplayer/playback/Track$Companion;", "", "()V", "albumArtUri", "Landroid/net/Uri;", "albumId", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.net.Uri albumArtUri(long albumId) {
            return null;
        }
    }
}