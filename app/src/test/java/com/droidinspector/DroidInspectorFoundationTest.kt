package com.droidinspector

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Droid Inspector X Foundation
 * Author: @Bienvenu
 */
class DroidInspectorFoundationTest {

    @Test
    fun testAppMetadata() {
        val appName = "DROID INSPECTOR X"
        val authorSignature = "@Bienvenu"

        assertEquals("DROID INSPECTOR X", appName)
        assertEquals("@Bienvenu", authorSignature)
    }

    @Test
    fun testBatteryPercentageCalculation() {
        val level = 42
        val scale = 100
        val percentage = (level * 100) / scale
        assertEquals(42, percentage)
        assertTrue(percentage in 0..100)
    }
}
