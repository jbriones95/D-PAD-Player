package com.example.dpadplayer.playback;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\u0018\u0000 _2\u00020\u0001:\u0002_`B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010A\u001a\u00020\u001dH\u0002J\b\u0010B\u001a\u00020CH\u0002J\b\u0010D\u001a\u00020\u001dH\u0002J\b\u0010E\u001a\u00020\u001dH\u0002J\u0006\u0010F\u001a\u00020\u001dJ\u0006\u0010G\u001a\u00020\u001dJ\u0012\u0010H\u001a\u00020I2\b\u0010J\u001a\u0004\u0018\u00010KH\u0016J\b\u0010L\u001a\u00020\u001dH\u0016J\b\u0010M\u001a\u00020\u001dH\u0016J\"\u0010N\u001a\u00020\n2\b\u0010J\u001a\u0004\u0018\u00010K2\u0006\u0010O\u001a\u00020\n2\u0006\u0010P\u001a\u00020\nH\u0016J\b\u0010Q\u001a\u00020\u001dH\u0002J\u000e\u0010R\u001a\u00020\u001d2\u0006\u0010S\u001a\u00020\nJ\u0006\u0010T\u001a\u00020\u001dJ\b\u0010U\u001a\u00020\u001dH\u0002J\u000e\u0010V\u001a\u00020\u001d2\u0006\u0010W\u001a\u00020\u000fJ\b\u0010X\u001a\u00020\u001dH\u0002J\b\u0010Y\u001a\u00020\u001dH\u0002J\u0006\u0010Z\u001a\u00020\u001dJ\b\u0010[\u001a\u00020\u001dH\u0002J\u0010\u0010\\\u001a\u00020\u001d2\u0006\u0010S\u001a\u00020\nH\u0002J\b\u0010]\u001a\u00020\u001dH\u0002J\b\u0010^\u001a\u00020\u001dH\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u00060\bR\u00020\u0000X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0014\u001a\u00020\u00158F\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082.\u00a2\u0006\u0002\n\u0000R(\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R(\u0010\"\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\u001f\"\u0004\b$\u0010!R(\u0010%\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\u001f\"\u0004\b\'\u0010!R(\u0010(\u001a\u0010\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010\u001f\"\u0004\b*\u0010!R(\u0010+\u001a\u0010\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b,\u0010\u001f\"\u0004\b-\u0010!R\u000e\u0010.\u001a\u00020/X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u000201X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u00102\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b3\u0010\r\"\u0004\b4\u00105R\u001a\u00106\u001a\u00020\u0015X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b7\u0010\u0016\"\u0004\b8\u00109R\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020\n0;X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010=\u001a\b\u0012\u0004\u0012\u00020>0;\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010@\u00a8\u0006a"}, d2 = {"Lcom/example/dpadplayer/playback/PlaybackService;", "Landroid/app/Service;", "()V", "audioFocusRequest", "Landroid/media/AudioFocusRequest;", "audioManager", "Landroid/media/AudioManager;", "binder", "Lcom/example/dpadplayer/playback/PlaybackService$LocalBinder;", "<set-?>", "", "currentIndex", "getCurrentIndex", "()I", "currentPosition", "", "getCurrentPosition", "()J", "duration", "getDuration", "isPlaying", "", "()Z", "mainHandler", "Landroid/os/Handler;", "mediaSession", "Landroid/support/v4/media/session/MediaSessionCompat;", "onPlaybackStateChanged", "Lkotlin/Function1;", "", "getOnPlaybackStateChanged", "()Lkotlin/jvm/functions/Function1;", "setOnPlaybackStateChanged", "(Lkotlin/jvm/functions/Function1;)V", "onPositionChanged", "getOnPositionChanged", "setOnPositionChanged", "onRepeatChanged", "getOnRepeatChanged", "setOnRepeatChanged", "onShuffleChanged", "getOnShuffleChanged", "setOnShuffleChanged", "onTrackChanged", "getOnTrackChanged", "setOnTrackChanged", "player", "Lcom/google/android/exoplayer2/ExoPlayer;", "positionRunnable", "Ljava/lang/Runnable;", "repeatMode", "getRepeatMode", "setRepeatMode", "(I)V", "shuffleOn", "getShuffleOn", "setShuffleOn", "(Z)V", "shuffleOrder", "", "shufflePos", "tracks", "Lcom/example/dpadplayer/playback/Track;", "getTracks", "()Ljava/util/List;", "abandonAudioFocus", "buildNotification", "Landroid/app/Notification;", "buildShuffleOrder", "createNotificationChannel", "cycleRepeat", "next", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onStartCommand", "flags", "startId", "pausePlayback", "prepareAndPlay", "index", "prev", "requestAudioFocusAndPlay", "seekTo", "positionMs", "startPositionPolling", "stopPositionPolling", "toggleShuffle", "tryStartForeground", "updateMetadata", "updateNotification", "updatePlaybackState", "Companion", "LocalBinder", "app_debug"})
public final class PlaybackService extends android.app.Service {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_PLAY = "com.example.dpadplayer.action.PLAY";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_PAUSE = "com.example.dpadplayer.action.PAUSE";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_NEXT = "com.example.dpadplayer.action.NEXT";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_PREV = "com.example.dpadplayer.action.PREV";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_SEEK_FWD = "com.example.dpadplayer.action.SEEK_FWD";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_SEEK_BWD = "com.example.dpadplayer.action.SEEK_BWD";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_SEEK_TO = "com.example.dpadplayer.action.SEEK_TO";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COMMAND_PLAY_INDEX = "com.example.dpadplayer.action.PLAY_INDEX";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EXTRA_INDEX = "index";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String EXTRA_POSITION = "position";
    public static final int REPEAT_OFF = 0;
    public static final int REPEAT_ALL = 1;
    public static final int REPEAT_ONE = 2;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String NOTIF_CHANNEL_ID = "dpad_player_channel";
    public static final int NOTIF_ID = 1;
    private static final long MEDIA_SESSION_ACTIONS = 823L;
    @org.jetbrains.annotations.NotNull
    private final com.example.dpadplayer.playback.PlaybackService.LocalBinder binder = null;
    private com.google.android.exoplayer2.ExoPlayer player;
    private android.support.v4.media.session.MediaSessionCompat mediaSession;
    private android.media.AudioManager audioManager;
    @org.jetbrains.annotations.Nullable
    private android.media.AudioFocusRequest audioFocusRequest;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.example.dpadplayer.playback.Track> tracks = null;
    private int currentIndex = 0;
    private boolean shuffleOn = false;
    private int repeatMode = 0;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Integer> shuffleOrder = null;
    private int shufflePos = 0;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onTrackChanged;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onPlaybackStateChanged;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onPositionChanged;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onShuffleChanged;
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onRepeatChanged;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler mainHandler = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable positionRunnable = null;
    @org.jetbrains.annotations.NotNull
    public static final com.example.dpadplayer.playback.PlaybackService.Companion Companion = null;
    
    public PlaybackService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.example.dpadplayer.playback.Track> getTracks() {
        return null;
    }
    
    public final int getCurrentIndex() {
        return 0;
    }
    
    public final boolean getShuffleOn() {
        return false;
    }
    
    public final void setShuffleOn(boolean p0) {
    }
    
    public final int getRepeatMode() {
        return 0;
    }
    
    public final void setRepeatMode(int p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> getOnTrackChanged() {
        return null;
    }
    
    public final void setOnTrackChanged(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Boolean, kotlin.Unit> getOnPlaybackStateChanged() {
        return null;
    }
    
    public final void setOnPlaybackStateChanged(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Long, kotlin.Unit> getOnPositionChanged() {
        return null;
    }
    
    public final void setOnPositionChanged(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Boolean, kotlin.Unit> getOnShuffleChanged() {
        return null;
    }
    
    public final void setOnShuffleChanged(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> getOnRepeatChanged() {
        return null;
    }
    
    public final void setOnRepeatChanged(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> p0) {
    }
    
    @java.lang.Override
    public void onCreate() {
    }
    
    @java.lang.Override
    public int onStartCommand(@org.jetbrains.annotations.Nullable
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override
    public void onDestroy() {
    }
    
    private final void requestAudioFocusAndPlay() {
    }
    
    private final void pausePlayback() {
    }
    
    private final void abandonAudioFocus() {
    }
    
    public final void prepareAndPlay(int index) {
    }
    
    public final void next() {
    }
    
    public final void prev() {
    }
    
    public final void toggleShuffle() {
    }
    
    public final void cycleRepeat() {
    }
    
    private final void buildShuffleOrder() {
    }
    
    public final void seekTo(long positionMs) {
    }
    
    public final boolean isPlaying() {
        return false;
    }
    
    public final long getCurrentPosition() {
        return 0L;
    }
    
    public final long getDuration() {
        return 0L;
    }
    
    private final void updatePlaybackState() {
    }
    
    private final void updateMetadata(int index) {
    }
    
    private final void createNotificationChannel() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final void tryStartForeground() {
    }
    
    private final void updateNotification() {
    }
    
    private final void startPositionPolling() {
    }
    
    private final void stopPositionPolling() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0012X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0012X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/example/dpadplayer/playback/PlaybackService$Companion;", "", "()V", "COMMAND_NEXT", "", "COMMAND_PAUSE", "COMMAND_PLAY", "COMMAND_PLAY_INDEX", "COMMAND_PREV", "COMMAND_SEEK_BWD", "COMMAND_SEEK_FWD", "COMMAND_SEEK_TO", "EXTRA_INDEX", "EXTRA_POSITION", "MEDIA_SESSION_ACTIONS", "", "NOTIF_CHANNEL_ID", "NOTIF_ID", "", "REPEAT_ALL", "REPEAT_OFF", "REPEAT_ONE", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"}, d2 = {"Lcom/example/dpadplayer/playback/PlaybackService$LocalBinder;", "Landroid/os/Binder;", "(Lcom/example/dpadplayer/playback/PlaybackService;)V", "getService", "Lcom/example/dpadplayer/playback/PlaybackService;", "app_debug"})
    public final class LocalBinder extends android.os.Binder {
        
        public LocalBinder() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.example.dpadplayer.playback.PlaybackService getService() {
            return null;
        }
    }
}