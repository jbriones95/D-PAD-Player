# DPad Player (Android)

Minimal D-pad friendly Android MP3 player built with Kotlin and ExoPlayer.

Features implemented:
- D-pad friendly focusable controls in a grid
- Play/Pause/Next/Prev/Seek
- Foreground service with media notification
- Simple recursive scan for .mp3 files under app external files (best-effort)

To build: open this folder in Android Studio and build the app.

Notes/limitations:
- The scanner is a simple recursive scan starting from the app external files parent; it is intentionally minimal to keep the sample small. For production, use MediaStore queries and handle scoped storage on Android 11+.
- Runtime permission READ_EXTERNAL_STORAGE is requested for API levels that require it.
- The app starts a foreground service to manage playback and show a media notification. MediaSession is used for media actions.
- D-pad navigation: the main controls are arranged in a 2x3 grid with large buttons that are focusable by D-pad; the track list is above and focusable.
