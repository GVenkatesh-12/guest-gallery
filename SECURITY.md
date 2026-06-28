# Security Policy

## Supported Versions

We actively support and fix security bugs on the following versions of Guest Gallery:

| Version | Supported |
| ------- | --------- |
| 1.0.x   | Yes       |

## Security Philosophy

Guest Gallery is built to ensure photos cannot leak:
1. **Zero Internet Permissions**: The app does not request the `android.permission.INTERNET` permission.
2. **Memory Only Sessions**: Shared photo URIs are resolved temporarily during the active session. The application does not write shared images to persistent disk folders.
3. **Prevention of Capture**: `FLAG_SECURE` is active by default to prevent screenshots and screen recordings of the viewer.

## Reporting a Vulnerability

If you discover a security vulnerability, please do not file a public issue. Instead, report it directly via email to the project maintainers. We will investigate the issue and coordinate a patch within 48 hours.
