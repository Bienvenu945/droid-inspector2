package com.droidinspector.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun formatsGigabytes() {
        assertEquals("1.5 Go", formatBytes(1536L * 1024L * 1024L))
    }

    @Test
    fun formatsMegabytes() {
        assertEquals("512 Mo", formatBytes(512L * 1024L * 1024L))
    }

    @Test
    fun formatsSmallValues() {
        assertEquals("64 Ko", formatBytes(64L * 1024L))
        assertEquals("12 o", formatBytes(12L))
    }

    @Test
    fun computesPercentages() {
        assertEquals(25f, percentOf(25L, 100L), 0.001f)
        assertEquals(0f, percentOf(10L, 0L), 0.001f)
    }
}
