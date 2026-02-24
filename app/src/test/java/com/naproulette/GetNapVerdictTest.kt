package com.naproulette

import com.naproulette.domain.usecase.GetNapVerdict
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

class GetNapVerdictTest {

    private val useCase = GetNapVerdict()

    @Test
    fun `short nap gets snarky message`() {
        val verdict = useCase(3.minutes)
        assertTrue(verdict.contains("blink"))
    }

    @Test
    fun `power nap gets NASA message`() {
        val verdict = useCase(20.minutes)
        assertTrue(verdict.contains("NASA"))
    }

    @Test
    fun `full cycle gets new person message`() {
        val verdict = useCase(100.minutes)
        assertTrue(verdict.contains("new person"))
    }

    @Test
    fun `very long nap gets lifestyle message`() {
        val verdict = useCase(150.minutes)
        assertTrue(verdict.contains("lifestyle"))
    }
}
