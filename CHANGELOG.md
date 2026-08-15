# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.0.0] - 2026-08-13

### Fixed
- Intercept and safely handle Back gestures while screen-pinned to prevent system closing conflicts and ANR crashes upon unpinning.
- Display screen-pinning exit guidance toast on Back gesture during pinned mode.
- Move cache cleanup off the main thread to prevent an ANR while exiting after viewing media.

## [3.0] - 2026-08-03

### Changed
- Wired settings into the viewer, security flow, appearance theme, and performance behavior.
- Smoothed screen-pinning guidance and application-wide motion transitions.
- Fixed static-analysis failures and reduced slider-driven recomposition churn.

## [1.0.0] - 2026-06-28

### Added
- Ephemeral photo viewing session created directly via Android Share Intent (`ACTION_SEND` and `ACTION_SEND_MULTIPLE`).
- Clean Architecture implementation with 7 modules: `:app`, `:core`, `:domain`, `:data`, `:viewer`, `:settings`, and `:security`.
- Horizontal pager with smooth swipe physics and zoom support (using Coil 3 and Telephoto for zoomable sub-sampled rendering).
- Dynamic dynamic wallpaper color support and Material 3 design elements (including premium custom animated toggle switches and Glassmorphism surfaces).
- Settings screen covering 67 separate toggles and sliders persisted via Android Jetpack DataStore Preferences.
- Device security lockups including `FLAG_SECURE` window properties to block screenshots and recording.
- Biometric verification interface using AndroidX Biometric to prevent unauthorized exits from secure viewing.
- Full immersive configuration hiding system navigation bars and status bars.
- CI/CD workflow designs for linting, testing, and generating signed release APKs on version tags.
