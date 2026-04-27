package com.example.dpadplayer;

/**
 * Full now-playing screen.
 * Back button + D-pad BACK key return to LibraryFragment.
 * D-pad LEFT/RIGHT on seekbar scrubs by the configured seek/skip duration.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u001eH\u0002J\u0006\u0010\"\u001a\u00020#J\b\u0010$\u001a\u00020#H\u0002J$\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010*2\b\u0010+\u001a\u0004\u0018\u00010,H\u0016J\b\u0010-\u001a\u00020#H\u0016J\u001a\u0010.\u001a\u00020#2\u0006\u0010/\u001a\u00020&2\b\u0010+\u001a\u0004\u0018\u00010,H\u0016J\b\u00100\u001a\u00020#H\u0002J\b\u00101\u001a\u00020#H\u0002J\b\u00102\u001a\u00020#H\u0002J\b\u00103\u001a\u00020#H\u0002J\u0010\u00104\u001a\u00020#2\u0006\u00105\u001a\u00020\u000fH\u0002J\u0010\u00106\u001a\u00020#2\u0006\u00107\u001a\u000208H\u0002J\b\u00109\u001a\u00020#H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0017\u001a\u00020\u00188BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006:"}, d2 = {"Lcom/example/dpadplayer/PlayerFragment;", "Landroidx/fragment/app/Fragment;", "()V", "albumArt", "Landroid/widget/ImageView;", "btnBack", "Lcom/google/android/material/button/MaterialButton;", "btnForward", "btnNext", "btnPlay", "btnPrev", "btnRepeat", "seekBar", "Landroid/widget/SeekBar;", "seekBarDragging", "", "tvAlbum", "Landroid/widget/TextView;", "tvArtist", "tvDuration", "tvPosition", "tvTitle", "tvTrackCounter", "viewModel", "Lcom/example/dpadplayer/MusicViewModel;", "getViewModel", "()Lcom/example/dpadplayer/MusicViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "configuredSeekStepMs", "", "formatMs", "", "ms", "navigateBack", "", "observeViewModel", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onViewCreated", "view", "refreshNowPlaying", "setupButtons", "setupSeekBar", "updateForwardSkipDescription", "updatePlayPauseIcon", "isPlaying", "updateRepeatIcon", "mode", "", "updateTrackCounter", "app_debug"})
public final class PlayerFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy viewModel$delegate = null;
    private com.google.android.material.button.MaterialButton btnBack;
    private android.widget.TextView tvTrackCounter;
    private android.widget.ImageView albumArt;
    private android.widget.TextView tvTitle;
    private android.widget.TextView tvArtist;
    private android.widget.TextView tvAlbum;
    private android.widget.TextView tvPosition;
    private android.widget.TextView tvDuration;
    private android.widget.SeekBar seekBar;
    private com.google.android.material.button.MaterialButton btnRepeat;
    private com.google.android.material.button.MaterialButton btnPrev;
    private com.google.android.material.button.MaterialButton btnPlay;
    private com.google.android.material.button.MaterialButton btnNext;
    private com.google.android.material.button.MaterialButton btnForward;
    private boolean seekBarDragging = false;
    
    public PlayerFragment() {
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
    
    public final void navigateBack() {
    }
    
    private final void setupButtons() {
    }
    
    private final void setupSeekBar() {
    }
    
    private final void observeViewModel() {
    }
    
    private final void refreshNowPlaying() {
    }
    
    private final void updatePlayPauseIcon(boolean isPlaying) {
    }
    
    private final void updateRepeatIcon(int mode) {
    }
    
    private final void updateForwardSkipDescription() {
    }
    
    private final void updateTrackCounter() {
    }
    
    private final long configuredSeekStepMs() {
        return 0L;
    }
    
    private final java.lang.String formatMs(long ms) {
        return null;
    }
}