package com.guestgallery.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Guest Gallery application entry point.
 *
 * Annotated with [HiltAndroidApp] to trigger Hilt's code generation
 * and serve as the application-level dependency container.
 * Coil 3.x auto-initializes via its AndroidX Startup Initializer,
 * so no manual image-loader setup is needed.
 */
@HiltAndroidApp
class GuestGalleryApplication : Application()
