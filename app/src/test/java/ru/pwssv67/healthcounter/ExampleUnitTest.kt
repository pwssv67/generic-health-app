package ru.pwssv67.healthcounter

import org.junit.Test

import org.junit.Assert.*
import ru.pwssv67.healthcounter.extensions.normalStringDateToShort

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun longDateToShortTest() {
        assert(normalStringDateToShort("2020-01-05") == "05.01")
    }
}
