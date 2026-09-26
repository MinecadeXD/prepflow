<div align="center">

# 📚 PrepFlow

**Study & MHT-CET Preparation Tracker**

[![Built with HTML](https://img.shields.io/badge/Frontend-HTML%2FCSS%2FJavaScript-E34F26?logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Cordova](https://img.shields.io/badge/Build-Apache_Cordova-E8E8E8?logo=apachecordova&logoColor=black)](https://cordova.apache.org)
[![GitHub Actions](https://img.shields.io/badge/Build-GitHub_Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![Version](https://img.shields.io/badge/Version-1.17.2-blue)](https://github.com/MinecadeXD/prepflow/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

### [📦 Download Latest Release](https://github.com/MinecadeXD/prepflow/releases/latest)

*An offline-friendly study tracker for organizing subjects and chapters, monitoring preparation progress, and managing MHT-CET study data.*

</div>

---

## Features

### 📊 Dashboard & Progress
- Overall chapter progress overview with **Total, Completed, In Progress, and Not Started** counts.
- Subject cards showing chapter counts, completion counts, and overall subject progress.
- **Active Chapters Focus** section for quickly returning to chapters currently in progress.
- Subject-level filtering for **All, In Progress, and Not Started** chapters.

### 📚 Chapter Tracking
- Track individual chapter completion based on multiple study activities.
- Record **lectures completed / total lectures**.
- Record **tests completed / total tests**.
- Mark **Textbook, Practice, Formula Sheet, and CET MCQ** work as completed.
- Set chapter priority levels: **High, Medium, or Low**.
- Track **revision count** and the date of the latest revision.
- Add chapter-specific **notes**.
- Automatically record the **last studied date**.
- View calculated chapter progress and completion status.

### 🗂️ Chapter Management
- Manage chapters for each subject from a dedicated management screen.
- Add, remove, and reorder chapters.
- Import or replace chapter lists using a simple text-based chapter editor.

### 💾 Data & Backup
- Store study data locally on the device for offline use.
- Export PrepFlow data to a backup file.
- Import a previously saved backup to restore study data.
- Clear all app data with a confirmation step.

### 📱 App Experience
- Dedicated Settings page for app and data management.
- Android back-button navigation between dashboard, subjects, chapters, settings, and dialogs.
- Lightweight single-page architecture designed for use as an Android app.
- No account or online service is required for normal study tracking.

## Current Version

**1.17.2**

## Technology

- HTML, CSS and JavaScript
- Apache Cordova for Android packaging
- Custom Cordova backup/restore plugin
- GitHub Actions for release APK builds

The main application is intentionally kept in a single `index.html` file to keep the project straightforward and easy to maintain.

## Project Structure

```text
prepflow/
├── .github/
│   └── workflows/
│       └── build-apk.yml
├── cordova-plugin-prepflow-backup/
│   ├── src/
│   │   └── android/
│   │       └── PrepFlowBackup.java
│   ├── www/
│   │   └── prepflow-backup.js
│   ├── package.json
│   └── plugin.xml
├── index.html
├── logo.png
└── README.md
```

## Backup Plugin

The repository includes `cordova-plugin-prepflow-backup`, a custom Cordova plugin that provides native Android backup and restore functionality through the **Android Storage Access Framework (SAF)**.

### What it does

The plugin connects the web application to two native Android actions:

- **Export backup** — receives the backup JSON from PrepFlow and opens the Android document picker so the user can choose where to save the backup file.
- **Import backup** — opens the Android document picker so the user can select an existing JSON backup file, then returns its contents to PrepFlow.

The plugin does not use a fixed app-specific storage path. Android's document picker handles the user's selected file location.

### How it works

```text
PrepFlow (JavaScript)
        │
        ├── exportBackup()
        │       ↓
        │   Cordova bridge
        │       ↓
        │   ACTION_CREATE_DOCUMENT
        │       ↓
        │   Android file picker
        │       ↓
        │   Selected JSON file
        │
        └── importBackup()
                ↓
            Cordova bridge
                ↓
            ACTION_OPEN_DOCUMENT
                ↓
            Android file picker
                ↓
            JSON contents
                ↓
            PrepFlow
```

### JavaScript API

The plugin exposes `PrepFlowBackup` with two methods:

```javascript
PrepFlowBackup.exportBackup(backupJson, filename, success, error);
PrepFlowBackup.importBackup(success, error);
```

**Export parameters**
- `backupJson` — backup data as a JSON string.
- `filename` — suggested filename shown by the Android save dialog.
- `success` — called after the file is successfully written.
- `error` — called if the operation fails or is cancelled.

**Import callbacks**
- `success` — receives the selected file contents as a UTF-8 string.
- `error` — called if the operation fails or is cancelled.

### Android implementation

The native implementation is in:

```text
cordova-plugin-prepflow-backup/src/android/PrepFlowBackup.java
```

It uses Android's `ACTION_CREATE_DOCUMENT` and `ACTION_OPEN_DOCUMENT` intents with `application/json` MIME type. Files are read and written through Android's `ContentResolver`.

The plugin is intentionally kept inside this repository because it is part of PrepFlow's Android build rather than a separately fetched application dependency.

## Building the Android APK

PrepFlow's Android release APK is built automatically with GitHub Actions using `.github/workflows/build-apk.yml`.

### Build process

The workflow runs on pushes to the `main` branch and:

1. Checks out the repository.
2. Sets up Java 17 and Node.js 20.
3. Installs Cordova locally.
4. Creates a temporary Cordova project with the PrepFlow package ID.
5. Copies `index.html` and `logo.png` into the Cordova web assets.
6. Generates the Cordova `config.xml` with the app name, version, author, icon, and Android settings.
7. Installs the local `cordova-plugin-prepflow-backup` plugin.
8. Adds the Android platform.
9. Verifies that the backup plugin and its Android registration were installed correctly.
10. Decodes the signing keystore from a GitHub Actions secret.
11. Generates the temporary Cordova signing configuration.
12. Builds the signed Android release APK.
13. Verifies that the APK was produced.
14. Removes the temporary signing files.
15. Uploads the APK as the `PrepFlow-Release-APK` GitHub Actions artifact.

### Required GitHub Actions secrets

The workflow expects these repository secrets:

- `KEYSTORE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

The signing keystore and passwords are never stored in the repository. Temporary signing files are removed after the APK build.

The generated APK can be downloaded from the workflow's **Artifacts** section after a successful build.

See [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) for the exact implementation.

## Development

Because PrepFlow is a single-page application, most application changes can be made directly in:

```text
index.html
```

The custom Android backup functionality is located in:

```text
cordova-plugin-prepflow-backup/
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for the full license text.

---

<div align="center">

### 👨‍💻 Developed by [Minecade](https://github.com/MinecadeXD)

</div>
