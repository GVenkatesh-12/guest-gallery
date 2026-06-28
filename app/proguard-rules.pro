# Add project specific ProGuard rules here.

# Keep Compose runtime
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.** { *; }

# Keep Coil
-keep class coil3.** { *; }

# Keep Telephoto
-keep class me.saket.telephoto.** { *; }

# Keep data classes used for DataStore
-keep class com.guestgallery.domain.model.** { *; }

# Keep BiometricPrompt
-keep class androidx.biometric.** { *; }

# Standard Android rules
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
