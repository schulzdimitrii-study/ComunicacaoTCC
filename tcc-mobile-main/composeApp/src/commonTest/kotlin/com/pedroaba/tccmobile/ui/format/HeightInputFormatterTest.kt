package com.pedroaba.tccmobile.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HeightInputFormatterTest {

    @Test
    fun `centimeters are parsed as meters`() {
        assertEquals(1.8, parseHeightCmToMeters("180"))
    }

    @Test
    fun `comma decimal centimeters are accepted`() {
        assertEquals(1.765, parseHeightCmToMeters("176,5"))
    }

    @Test
    fun `meters are formatted as centimeters`() {
        assertEquals("180", formatHeightMetersToCm(1.8))
    }

    @Test
    fun `invalid height returns null`() {
        assertNull(parseHeightCmToMeters("abc"))
    }
}
