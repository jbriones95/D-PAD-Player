package com.example.dpadplayer;

/**
 * Library screen: full track list + Auxio-style mini-player bar at bottom.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u001a\u001a\u00020\u001bJ\b\u0010\u001c\u001a\u00020\u001dH\u0002J$\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\u000e\u0010%\u001a\u00020\u001b2\u0006\u0010&\u001a\u00020\'J\u0006\u0010(\u001a\u00020\u001bJ\b\u0010)\u001a\u00020\u001dH\u0016J\u001a\u0010*\u001a\u00020\u001d2\u0006\u0010+\u001a\u00020\u000e2\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\u0006\u0010,\u001a\u00020\u0013J\b\u0010-\u001a\u00020\u001dH\u0002J\u0010\u0010.\u001a\u00020\u001d2\u0006\u0010/\u001a\u00020\u001bH\u0002J\u0010\u00100\u001a\u00020\u001d2\u0006\u00101\u001a\u000202H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017\u00a8\u00063"}, d2 = {"Lcom/example/dpadplayer/LibraryFragment;", "Landroidx/fragment/app/Fragment;", "()V", "adapter", "Lcom/example/dpadplayer/TrackAdapter;", "btnSettings", "Lcom/google/android/material/button/MaterialButton;", "miniArt", "Landroid/widget/ImageView;", "miniArtist", "Landroid/widget/TextView;", "miniBtnNext", "miniBtnPlay", "miniPlayer", "Landroid/view/View;", "miniProgress", "Lcom/google/android/material/progressindicator/LinearProgressIndicator;", "miniTitle", "recycler", "Landroidx/recyclerview/widget/RecyclerView;", "viewModel", "Lcom/example/dpadplayer/MusicViewModel;", "getViewModel", "()Lcom/example/dpadplayer/MusicViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "isMiniPlayerFocused", "", "observeViewModel", "", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDpadDown", "focusedPos", "", "onDpadUpFromMini", "onResume", "onViewCreated", "view", "recyclerView", "refreshMiniPlayer", "updateMiniPlayIcon", "isPlaying", "updateProgressBar", "pos", "", "app_debug"})
public final class LibraryFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy viewModel$delegate = null;
    private androidx.recyclerview.widget.RecyclerView recycler;
    private com.example.dpadplayer.TrackAdapter adapter;
    private com.google.android.material.button.MaterialButton btnSettings;
    private android.view.View miniPlayer;
    private android.widget.ImageView miniArt;
    private android.widget.TextView miniTitle;
    private android.widget.TextView miniArtist;
    private com.google.android.material.button.MaterialButton miniBtnPlay;
    private com.google.android.material.button.MaterialButton miniBtnNext;
    private com.google.android.material.progressindicator.LinearProgressIndicator miniProgress;
    
    public LibraryFragment() {
        super();
    }
    
    private final com.example.dpadplayer.MusicViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    public void onResume() {
    }
    
    private final void observeViewModel() {
    }
    
    private final void refreshMiniPlayer() {
    }
    
    private final void updateMiniPlayIcon(boolean isPlaying) {
    }
    
    private final void updateProgressBar(long pos) {
    }
    
    public final boolean onDpadDown(int focusedPos) {
        return false;
    }
    
    public final boolean onDpadUpFromMini() {
        return false;
    }
    
    public final boolean isMiniPlayerFocused() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.recyclerview.widget.RecyclerView recyclerView() {
        return null;
    }
}