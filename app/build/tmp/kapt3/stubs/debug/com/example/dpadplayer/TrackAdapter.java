package com.example.dpadplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001eB?\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007\u0012\u0016\b\u0002\u0010\n\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\b\u0010\u0012\u001a\u00020\bH\u0016J\u001c\u0010\u0013\u001a\u00020\t2\n\u0010\u0014\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0015\u001a\u00020\bH\u0016J\u001c\u0010\u0016\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\bH\u0016J\u000e\u0010\u001a\u001a\u00020\t2\u0006\u0010\u001b\u001a\u00020\bJ\u0014\u0010\u001c\u001a\u00020\t2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0010\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/example/dpadplayer/TrackAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/dpadplayer/TrackAdapter$VH;", "items", "", "Lcom/example/dpadplayer/playback/Track;", "onTrackClick", "Lkotlin/Function1;", "", "", "onTrackLongClick", "", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "selectedIndex", "formatMs", "", "ms", "", "getItemCount", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setSelectedIndex", "index", "updateTracks", "newItems", "VH", "app_debug"})
public final class TrackAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.dpadplayer.TrackAdapter.VH> {
    @org.jetbrains.annotations.NotNull
    private java.util.List<com.example.dpadplayer.playback.Track> items;
    @org.jetbrains.annotations.NotNull
    private final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> onTrackClick = null;
    @org.jetbrains.annotations.Nullable
    private final kotlin.jvm.functions.Function1<java.lang.Integer, java.lang.Boolean> onTrackLongClick = null;
    private int selectedIndex = -1;
    
    public TrackAdapter(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.dpadplayer.playback.Track> items, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onTrackClick, @org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Integer, java.lang.Boolean> onTrackLongClick) {
        super();
    }
    
    public final void updateTracks(@org.jetbrains.annotations.NotNull
    java.util.List<com.example.dpadplayer.playback.Track> newItems) {
    }
    
    public final void setSelectedIndex(int index) {
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public com.example.dpadplayer.TrackAdapter.VH onCreateViewHolder(@org.jetbrains.annotations.NotNull
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull
    com.example.dpadplayer.TrackAdapter.VH holder, int position) {
    }
    
    @java.lang.Override
    public int getItemCount() {
        return 0;
    }
    
    private final java.lang.String formatMs(long ms) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\f\u00a8\u0006\u0014"}, d2 = {"Lcom/example/dpadplayer/TrackAdapter$VH;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/example/dpadplayer/TrackAdapter;Landroid/view/View;)V", "art", "Landroid/widget/ImageView;", "getArt", "()Landroid/widget/ImageView;", "artist", "Landroid/widget/TextView;", "getArtist", "()Landroid/widget/TextView;", "duration", "getDuration", "indicator", "getIndicator", "()Landroid/view/View;", "title", "getTitle", "app_debug"})
    public final class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull
        private final android.widget.ImageView art = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView title = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView artist = null;
        @org.jetbrains.annotations.NotNull
        private final android.widget.TextView duration = null;
        @org.jetbrains.annotations.NotNull
        private final android.view.View indicator = null;
        
        public VH(@org.jetbrains.annotations.NotNull
        android.view.View view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.ImageView getArt() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getTitle() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getArtist() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.widget.TextView getDuration() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final android.view.View getIndicator() {
            return null;
        }
    }
}