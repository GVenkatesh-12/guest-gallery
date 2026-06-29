package com.guestgallery.core.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

private const val BYTES_PER_UNIT = 1024

/**
 * Returns the display name for a content URI, or null if unavailable.
 */
fun Uri.getDisplayName(context: Context): String? {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) cursor.getString(nameIndex) else null
        } else {
            null
        }
    }
}

/**
 * Returns the file size in bytes for a content URI, or null if unavailable.
 */
fun Uri.getFileSize(context: Context): Long? {
    return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else null
        } else {
            null
        }
    }
}

/**
 * Returns a human-readable file size string (e.g., "2.4 MB").
 */
fun Long.toHumanReadableSize(): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = this.toDouble()
    var unitIndex = 0
    while (size >= BYTES_PER_UNIT && unitIndex < units.lastIndex) {
        size /= BYTES_PER_UNIT
        unitIndex++
    }
    return if (unitIndex == 0) {
        "${size.toInt()} ${units[unitIndex]}"
    } else {
        "%.1f %s".format(size, units[unitIndex])
    }
}

/**
 * Returns the MIME type for a content URI.
 */
fun Uri.getMimeType(context: Context): String? {
    return context.contentResolver.getType(this)
}
