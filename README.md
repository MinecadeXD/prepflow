# PrepFlow

PrepFlow is a lightweight study and exam-tracking app designed for 11th Science and MHT-CET preparation.

It provides a simple way to organize subjects and chapters, track study progress, and manage study data locally.

## Features

- Subject and chapter management
- Study progress tracking
- MHT-CET preparation support
- Local/offline data storage
- Backup and restore of app data
- Import and export support
- Clear-all-data option with confirmation
- Android back navigation support
- Lightweight single-page app architecture

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

The repository includes `cordova-plugin-prepflow-backup`, a custom Cordova plugin that provides native Android backup and restore functionality using the Android Storage Access Framework.

The plugin is kept in the repository because it is part of the application's Android build and is not an external dependency that needs to be fetched separately.

## Building the Android APK

The repository uses GitHub Actions to build the Android release APK.

The workflow:

1. Checks out the repository.
2. Sets up Java 17 and Node.js 20.
3. Installs Cordova.
4. Creates a temporary Cordova Android project.
5. Copies the PrepFlow application files into the project.
6. Adds the local backup plugin.
7. Adds the Android platform.
8. Builds a signed release APK using GitHub repository secrets.
9. Uploads the resulting APK as a GitHub Actions artifact.

The signing credentials are supplied through GitHub Actions secrets and are not stored in this repository.

See `.github/workflows/build-apk.yml` for the complete build workflow.

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

## Developer

**Minecade**
