package com.droidinspector.core.utils

import java.util.Locale

/** Formats a size in bytes as "1.5 Go", "512 Mo", "64 Ko" or "12 o". */
fun formatBytes(bytes: Long): String {
    val kib = 1024.0
    val mib = kib * 1024.0
    val gib = mib * 1024.0
    val value = bytes.toDouble()
    return when {
        value >= gib -> String.format(Locale.US, "%.1f Go", value / gib)
        value >= mib -> String.format(Locale.US, "%.0f Mo", value / mib)
        value >= kib -> String.format(Locale.US, "%.0f Ko", value / kib)
        else -> "$bytes o"
    }
}

/** Percentage (0 to 100) of [part] in [total]; 0 when [total] is not positive. */
fun percentOf(part: Long, total: Long): Float {
    return if (total > 0L) part * 100f / total else 0f
}
