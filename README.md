# D-PAD Tools

D-PAD Tools is a small multi-purpose repository containing:

- An Android application (D-PAD Player) located in the `app/` module
- A tiny example npm package `dpad-tools` (minimal entrypoint) and a GitHub Actions workflow that can publish it to npmjs.org
- Convenience scripts and examples to build, install, and capture screenshots from a connected Android debug device

This README explains the Android app, how to build and install it to a USB-connected device, how to capture screenshots and screen recordings for demos, and how to publish the npm package using the provided Actions workflow.

## Android app — D-PAD Player

Location: `app/`

What it is: a media player demo app (D-PAD Player) built with Android Gradle. The app's applicationId is `com.example.dpadplayer`.

Quick start — build and install to a connected USB debug device

1. Ensure Android SDK, platform-tools (adb), and Java are installed and configured on your machine.
2. Build the debug APK and install it on the connected device:

```bash
./gradlew clean assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
```

3. Start the app on the device (or use your launcher):

```bash
adb shell am start -n com.example.dpadplayer/.MainActivity
```

Capture screenshots (already provided)

I captured sample screenshots during testing and placed them at the repository root as `screen1.png`, `screen2.png`, and `screen3.png`.

To capture screenshots yourself:

```bash

Record a short screen video

```bash

Notes about build timestamps

Gradle may reuse previously-built artifacts and report tasks as UP-TO-DATE. If the app shows an older "built at" time in its UI, run a clean build to force regeneration:

```bash

If you want the app to display the build time inside the UI, we can add a BuildConfig field that embeds the build timestamp at compile time — say `BuildConfig.BUILD_TIME` — and show it in the app.

## npm package — dpad-tools

Location: package.json and index.js at the repository root. The package name in package.json is `dpad-tools` and currently at version `0.1.0`.

Publishing via GitHub Actions

This repository contains a workflow `.github/workflows/publish-npm.yml` which will publish the package to npmjs.org when you push an annotated git tag matching the pattern `v*` (for example `v0.1.0`). The workflow expects a repository secret named `NPM_TOKEN` containing an npm automation token.

Steps to enable publishing:

1. Create an npm token at https://www.npmjs.com/settings/<your-username>/tokens
2. Add that token to the repository as an Actions secret named `NPM_TOKEN`
3. Create and push an annotated tag:

```bash
git add package.json index.js README.md .github/
git commit -m "chore: release v0.1.0"
git tag -a v0.1.0 -m "release v0.1.0"
git push origin HEAD --follow-tags
```

The workflow will run and publish the package as public to npm.

## Screenshots and Demo Assets

Included in the repo root:

- screen1.png
- screen2.png
- screen3.png

If you want these embedded into the README as a showcase, I can commit a README section demonstrating the UI with captions.

## Development notes

- The Android project uses Gradle 8.9. If you see deprecation warnings, they come from plugin or compile settings and should be addressed before upgrading to Gradle 9.
- The app currently prints several warnings about deprecated APIs (ExoPlayer and MediaItem usages). These are warnings only and do not block building.

## Suggested next tasks

1. Add a LICENSE file (MIT or choose a license).
2. Add author and repository fields to package.json if you intend to publish to npm.
3. Add a BuildConfig build time field so the UI can show the actual build timestamp.
4. Optionally embed the screenshots in the README and create a GitHub release with those images attached.

If you want me to make any of the suggested changes (add BuildConfig field, update package.json metadata, commit screenshots into README, create a release), tell me which and I'll implement them.
