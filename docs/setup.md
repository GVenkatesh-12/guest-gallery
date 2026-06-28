# Developer Setup & Run Guide

Follow these steps to set up, build, and verify the Guest Gallery project.

## Requirements

1. **Android Studio**: Ladybug | 2024.2.1 or newer.
2. **JDK**: Version 17 is required (or newer). Configure this under Studio Settings -> Build Tools -> Gradle -> Gradle JDK.
3. **Android Device**: Physical device or emulator running Android 8.0 (API 26) or higher. Biometric authentication requires a physical device with a fingerprint sensor or lock screen enabled.

## Initial Setup

1. **Clone the project**:
   ```bash
   git clone https://github.com/guestgallery/guest-gallery.git
   cd guest-gallery
   ```
2. **Download fonts**:
   Execute the font script to populate the Inter assets:
   ```bash
   chmod +x scripts/download-fonts.sh
   ./scripts/download-fonts.sh
   ```

## Development Commands

### Building Debug Version
To compile and assemble the debug application:
```bash
./gradlew assembleDebug
```

### Running Analysis & Formatting
To make sure formatting and static lint checks pass:
```bash
# Verify formatting
./gradlew ktlintCheck

# Auto-format files
./gradlew ktlintFormat

# Run detekt rules
./gradlew detekt
```

### Running Tests
To execute all local unit tests:
```bash
./gradlew testDebugUnitTest
```
To run Android instrumentation tests:
```bash
./gradlew connectedDebugAndroidTest
```
