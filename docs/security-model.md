# Security Model & Lockdown Implementation

Guest Gallery is engineered to protect user privacy when handing over a phone. This document details how its security measures operate.

## 1. Zero Network Access
The application does not declare the `android.permission.INTERNET` permission in its `AndroidManifest.xml`. 
This physically prevents the application from making any network calls, ensuring photos cannot leave the device.

## 2. Secure Switcher flag (`FLAG_SECURE`)
When secure window flags are enabled, the app applies `WindowManager.LayoutParams.FLAG_SECURE` to the active window:
- System screenshot captures are blocked.
- Screen recording apps receive a blank/black frame instead of the application window.
- In the Android system overview (recent apps list), the preview for Guest Gallery appears blank/black, preventing passersby from seeing what photos were open.

## 3. Ephemeral Sessions
- Shared image `content://` URIs are handled temporarily.
- Image records are stored in RAM within an active ViewModel state. They are never cached to disk databases.
- When the viewer is closed (either by completing auth or manual exit), the session is explicitly destroyed, references are cleared, and temporary cache directories are purged.

## 4. Immersive Mode
System UI components are hidden when immersive viewer modes are toggled:
- The system status bar (network info, time, battery) and notifications dropdown are hidden.
- The system navigation bar (Back/Home/Recent gestures) is hidden.
- Bars can only be revealed via transient swipe gestures, protecting against accidental navigation exits.

## 5. Screen Pinning Integration
Guest Gallery detects if system-level App Pinning is supported and active.
- When screen pinning is enabled, the user cannot press Home or Recent buttons or use swipe gestures to leave the app unless they perform the system shortcut to unpin.
- Unpinning automatically locks the device or requires the lock screen password/biometrics.
- If pinning is not active, the app prompts a guided dialog guiding the user to enable it for complete device lockdown.
