# Web2APK Builder — Private GitHub Actions MVP

A personal, mobile-friendly workflow for building Android WebView APKs from a website URL using GitHub Actions. No Termux or computer is required for the normal build flow.

## What this version does

- Builds a real Android APK in GitHub Actions.
- Lets you enter app name, website URL, package ID, version, orientation, screen mode, external-link behavior, pull-to-refresh and exit confirmation in the Actions form.
- Uploads the APK as a downloadable workflow artifact.
- Uses a native Android WebView wrapper, local APK signing performed by the Android Gradle build.
- Does not require a paid cloud build server.

## Important limitations

This is the **first working URL-based MVP**, not a full clone of every feature in FreeWebToApk yet.
- It wraps a website URL. HTML paste/file/ZIP import, custom icon upload, splash video, AdMob, native permission toggles, custom JavaScript/CSS editor, AAB output, and remote update settings are not implemented in this MVP.
- It builds a debug-signed APK suitable for testing and personal installation. For Play Store publishing, configure a private release signing key using GitHub Secrets and implement a release build.
- GitHub Actions usage for private repositories depends on your account's included minutes/storage and current GitHub limits. This project avoids paid services but cannot guarantee unlimited free builds.
- The app needs internet access to load remote websites. Offline games require bundled local assets, which are not part of this first version.
- Some websites may not work correctly inside Android WebView.

## Exact mobile-browser setup

1. Open https://github.com and sign in.
2. Tap **+** → **New repository**.
3. Repository name: `web2apk-builder`.
4. Select **Private**. Do not select a public repository if you want your source private.
5. Create the repository.
6. Open the repository, choose **Add file** → **Upload files**.
7. Upload the contents of this ZIP (the files and folders inside it, not the ZIP itself). On Android, extract the ZIP first using your file manager.
8. Commit the files to the `main` branch.
9. Open the repository's **Actions** tab. If prompted, enable Actions for the repository.
10. Select **Build Web2APK** from the workflows list, then tap **Run workflow**.
11. Enter:
    - **App name**: name shown below the app icon (maximum 30 characters recommended).
    - **Website URL**: a complete `https://` URL.
    - **Package ID**: e.g. `com.example.mywebapp` (use your own unique ID).
    - **Version name**: e.g. `1.0.0`.
    - Choose orientation and behavior options.
12. Tap **Run workflow** again to start the build.
13. Open the running workflow, wait for all steps to complete successfully, then scroll to **Artifacts**.
14. Download `web2apk-debug-apk`, extract it if GitHub gives you a ZIP, and install the `.apk` on your Android phone. Android may ask you to allow installation from that browser/file manager.

## If Actions is missing

- Confirm you are in the repository, not your profile.
- Open **Settings** → **Actions** → **General**.
- Under Actions permissions, allow GitHub Actions for this repository.
- Reopen the Actions tab.

## Build settings

The workflow validates common unsafe or malformed values and passes settings into the Android project at build time. The APK does not contain a GitHub token or repository credential.

## Testing

The ZIP should include the Android project, workflow, and this README. The GitHub Actions workflow runs Gradle compilation and uploads the APK only if the build succeeds. The project has not been built on your own device; the first GitHub Actions run is the definitive environment test.

## Troubleshooting

- **Workflow fails on URL**: include `https://`, and make sure the site is publicly reachable.
- **Invalid package ID**: use lowercase letters/digits separated by dots, with at least two segments, e.g. `com.example.myapp`.
- **Build fails**: open the workflow run → failing step → copy the first actual error and share it. Do not repeatedly change unrelated files.
- **APK cannot install**: remove a previous app with the same package ID if it was signed differently, then install again.
- **App opens blank**: test the website URL in Chrome first; the site may block WebView or require unsupported features.

## Project structure

```text
.
├── .github/workflows/build-web2apk.yml
├── app/build.gradle
├── app/proguard-rules.pro
├── app/src/main/AndroidManifest.xml
├── app/src/main/java/com/example/web2apk/MainActivity.kt
├── app/src/main/res/drawable/ic_launcher.xml
├── app/src/main/res/values/styles.xml
├── app/src/main/res/values/strings.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```
