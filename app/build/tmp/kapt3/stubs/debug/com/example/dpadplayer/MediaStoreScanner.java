package com.example.dpadplayer;

/**
 * Scans all MediaStore audio volumes (external + internal) and enriches each
 * track with full ID3/VorbisComment metadata via MediaMetadataRetriever.
 *
 * Strategy (mirrors Auxio's approach, adapted for Android MediaStore):
 * 1. Query MediaStore for file URIs + basic columns (fast bulk read).
 * 2. For each file, open MediaMetadataRetriever to read proper ID3 tags:
 *      title, artist, albumArtist, album, track#, disc#, year, genre, sortTitle, etc.
 * 3. Fall back to MediaStore column values when a tag is absent.
 * 4. Include is_pending files (freshly copied via adb push) via setIncludePending.
 * 5. Filter: duration >= 30 s OR duration IS NULL (pending files have NULL duration).
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0010B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J$\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00042\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0018\u0010\r\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\nH\u0002J\u001e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a8\u0006\u0011"}, d2 = {"Lcom/example/dpadplayer/MediaStoreScanner;", "", "()V", "applySortOrder", "", "Lcom/example/dpadplayer/playback/Track;", "tracks", "sortOrder", "", "collectRawRows", "Lcom/example/dpadplayer/MediaStoreScanner$RawRow;", "context", "Landroid/content/Context;", "enrichWithRetriever", "row", "loadTracks", "RawRow", "app_debug"})
public final class MediaStoreScanner {
    @org.jetbrains.annotations.NotNull
    public static final com.example.dpadplayer.MediaStoreScanner INSTANCE = null;
    
    private MediaStoreScanner() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.example.dpadplayer.playback.Track> loadTracks(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    java.lang.String sortOrder) {
        return null;
    }
    
    private final java.util.List<com.example.dpadplayer.MediaStoreScanner.RawRow> collectRawRows(android.content.Context context) {
        return null;
    }
    
    private final com.example.dpadplayer.playback.Track enrichWithRetriever(android.content.Context context, com.example.dpadplayer.MediaStoreScanner.RawRow row) {
        return null;
    }
    
    private final java.util.List<com.example.dpadplayer.playback.Track> applySortOrder(java.util.List<com.example.dpadplayer.playback.Track> tracks, java.lang.String sortOrder) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u001a\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\u0007H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u000eH\u00c6\u0003Jc\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u000eH\u00c6\u0001J\u0013\u0010&\u001a\u00020\u000e2\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020)H\u00d6\u0001J\t\u0010*\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0012R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b\u00a8\u0006+"}, d2 = {"Lcom/example/dpadplayer/MediaStoreScanner$RawRow;", "", "id", "", "uri", "Landroid/net/Uri;", "msTitle", "", "msArtist", "msAlbum", "msAlbumId", "msDuration", "msDateAdded", "isInternal", "", "(JLandroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJJZ)V", "getId", "()J", "()Z", "getMsAlbum", "()Ljava/lang/String;", "getMsAlbumId", "getMsArtist", "getMsDateAdded", "getMsDuration", "getMsTitle", "getUri", "()Landroid/net/Uri;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
    static final class RawRow {
        private final long id = 0L;
        @org.jetbrains.annotations.NotNull
        private final android.net.Uri uri = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String msTitle = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String msArtist = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String msAlbum = null;
        private final long msAlbumId = 0L;
        private final long msDuration = 0L;
        private final long msDateAdded = 0L;
        private final boolean isInternal = false;
        
        public RawRow(long id, @org.jetbrains.annotations.NotNull
        android.net.Uri uri, @org.jetbrains.annotations.NotNull
        java.lang.String msTitle, @org.jetbrains.annotations.NotNull
        java.lang.String msArtist, @org.jetbrains.annotations.NotNull
        java.lang.String msAlbum, long msAlbumId, long msDuration, long msDateAdded, boolean isInternal) {
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
        public final java.lang.String getMsTitle() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getMsArtist() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getMsAlbum() {
            return null;
        }
        
        public final long getMsAlbumId() {
            return 0L;
        }
        
        public final long getMsDuration() {
            return 0L;
        }
        
        public final long getMsDateAdded() {
            return 0L;
        }
        
        public final boolean isInternal() {
            return false;
        }
        
        public final long component1() {
            return 0L;
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
        
        public final long component6() {
            return 0L;
        }
        
        public final long component7() {
            return 0L;
        }
        
        public final long component8() {
            return 0L;
        }
        
        public final boolean component9() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.example.dpadplayer.MediaStoreScanner.RawRow copy(long id, @org.jetbrains.annotations.NotNull
        android.net.Uri uri, @org.jetbrains.annotations.NotNull
        java.lang.String msTitle, @org.jetbrains.annotations.NotNull
        java.lang.String msArtist, @org.jetbrains.annotations.NotNull
        java.lang.String msAlbum, long msAlbumId, long msDuration, long msDateAdded, boolean isInternal) {
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