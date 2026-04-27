D-PAD Tools
===============

A lightweight Android music player adapted for D-Pad (Android TV / Android TV-like) navigation and remote-friendly UX. This repository contains the Android app source, focus utilities, and playback service used during development.

Quick start
1. Install JDK 17 and set JAVA_HOME to its root (the project uses some Kotlin/Gradle features best with Java 17).
2. Build debug APK: `JAVA_HOME=/tmp/jdk17 ./gradlew :app:assembleDebug -x lint`
3. Install on device: `adb -s <device-id> install -r app/build/outputs/apk/debug/app-debug.apk`

Notes for visitors
- This repo includes development assets and generated files. The build output and some generated images may be tracked. See `.gitignore` for items we recommend excluding locally.
- For maintainers: the primary app sources are under `app/src/main/java` and resources under `app/src/main/res`.

Contributions
- Please open issues or pull requests. For major changes, discuss the design first (focus behaviour and player control changes are subtle).

License
- This repository is licensed under GPL-3.0. See LICENSE for details.
