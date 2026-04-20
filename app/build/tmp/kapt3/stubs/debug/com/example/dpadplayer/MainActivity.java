package com.example.dpadplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0007\u0018\u0000 52\u00020\u0001:\u00015B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u0006\u0010\u0017\u001a\u00020\u0014J\u0010\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dH\u0002J\b\u0010\u001f\u001a\u00020\u0004H\u0002J\n\u0010 \u001a\u0004\u0018\u00010!H\u0002J\u0012\u0010\"\u001a\u00020\u00142\b\u0010#\u001a\u0004\u0018\u00010$H\u0014J\b\u0010%\u001a\u00020\u0014H\u0002J\b\u0010&\u001a\u00020\u0014H\u0014J\b\u0010\'\u001a\u00020\u0014H\u0014J\u0006\u0010(\u001a\u00020\u0014J\u0006\u0010)\u001a\u00020\u0014J\u000e\u0010*\u001a\u00020\u00142\u0006\u0010+\u001a\u00020,J\u000e\u0010-\u001a\u00020\u00142\u0006\u0010.\u001a\u00020/J\u000e\u00100\u001a\u00020\u00142\u0006\u00101\u001a\u00020\nJ\b\u00102\u001a\u00020\u0014H\u0002J\u0006\u00103\u001a\u00020\u0014J\u0006\u00104\u001a\u00020\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010\u00a8\u00066"}, d2 = {"Lcom/example/dpadplayer/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "bound", "", "connection", "Landroid/content/ServiceConnection;", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "", "service", "Lcom/example/dpadplayer/playback/PlaybackService;", "viewModel", "Lcom/example/dpadplayer/MusicViewModel;", "getViewModel", "()Lcom/example/dpadplayer/MusicViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "checkPermissionsAndLoad", "", "currentTrack", "Lcom/example/dpadplayer/playback/Track;", "cycleRepeat", "dispatchKeyEvent", "event", "Landroid/view/KeyEvent;", "isDescendantOf", "view", "Landroid/view/View;", "parent", "isPlayerVisible", "libraryFragment", "Lcom/example/dpadplayer/LibraryFragment;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onPermissionGranted", "onStart", "onStop", "openPlayer", "openSettings", "playTrack", "index", "", "seekTo", "posMs", "", "sendCmd", "cmd", "setupServiceCallbacks", "togglePlayPause", "toggleShuffle", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy viewModel$delegate = null;
    @org.jetbrains.annotations.Nullable
    private com.example.dpadplayer.playback.PlaybackService service;
    private boolean bound = false;
    @org.jetbrains.annotations.NotNull
    private final android.content.ServiceConnection connection = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> requestPermissionLauncher = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_LIBRARY = "library";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_PLAYER = "player";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG_SETTINGS = "settings";
    @org.jetbrains.annotations.NotNull
    public static final com.example.dpadplayer.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.dpadplayer.MusicViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    public final void openSettings() {
    }
    
    public final void openPlayer() {
    }
    
    private final boolean isPlayerVisible() {
        return false;
    }
    
    @java.lang.Override
    public boolean dispatchKeyEvent(@org.jetbrains.annotations.NotNull
    android.view.KeyEvent event) {
        return false;
    }
    
    public final void togglePlayPause() {
    }
    
    public final void playTrack(int index) {
    }
    
    public final void seekTo(long posMs) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.dpadplayer.playback.Track currentTrack() {
        return null;
    }
    
    public final void toggleShuffle() {
    }
    
    public final void cycleRepeat() {
    }
    
    public final void sendCmd(@org.jetbrains.annotations.NotNull
    java.lang.String cmd) {
    }
    
    private final void setupServiceCallbacks() {
    }
    
    private final void checkPermissionsAndLoad() {
    }
    
    private final void onPermissionGranted() {
    }
    
    private final com.example.dpadplayer.LibraryFragment libraryFragment() {
        return null;
    }
    
    private final boolean isDescendantOf(android.view.View view, android.view.View parent) {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/example/dpadplayer/MainActivity$Companion;", "", "()V", "TAG_LIBRARY", "", "TAG_PLAYER", "TAG_SETTINGS", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}