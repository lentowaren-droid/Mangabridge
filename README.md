# MangaBridge Android Preview

A personal-use Android manga reader shell built with Kotlin and Jetpack Compose.

## Current preview
- Dark home/library interface
- Discover screen with local demo catalogue
- Reader shell
- Premiumize provider placeholder
- Settings screen
- GitHub Actions workflow to build a debug APK in the cloud

## Important
This is a UI foundation, not a finished manga reader. Search results are demo data. Premiumize is not yet connected, and the reader does not yet load chapter images. Do not enter API credentials into this preview.

## Build APK in GitHub Actions
1. Create a private GitHub repository and upload this project.
2. Push the `main` branch.
3. Open **Actions** and run **Build MangaBridge APK**.
4. Download the `MangaBridge-debug-apk` artifact from the completed workflow.

The generated debug APK can be installed on your own Android device after enabling installation from the source used to download it.
