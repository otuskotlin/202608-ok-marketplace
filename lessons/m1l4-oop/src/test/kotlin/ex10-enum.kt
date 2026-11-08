import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Тема 10: enum class.

// Простой enum - фиксированный набор именованных констант.
enum class Direction { NORTH, SOUTH, EAST, WEST }

// enum с конструктором и свойствами - у каждой константы свой набор значений.
enum class Planet(val massKg: Double, val radiusM: Double) {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6);

    // Обычный метод, доступен всем константам.
    fun surfaceGravity(): Double = G * massKg / (radiusM * radiusM)

    companion object {
        private const val G = 6.67300E-11
    }
}

// enum может реализовывать интерфейс, и каждая константа - переопределить метод своим телом.
interface Op {
    fun apply(a: Int, b: Int): Int
}

enum class Operation : Op {
    PLUS {
        override fun apply(a: Int, b: Int) = a + b
    },
    TIMES {
        override fun apply(a: Int, b: Int) = a * b
    },
}

class EnumTest {

    @Test
    fun enumWithConstructorArgs() {
        assertEquals(5.976e+24, Planet.EARTH.massKg)
        assertEquals(3, Planet.entries.size)
    }

    @Test
    fun enumWithPerConstantBody() {
        assertEquals(7, Operation.PLUS.apply(3, 4))
        assertEquals(12, Operation.TIMES.apply(3, 4))
    }

    @Test
    fun safeParsingWithRunCatching() {
        val parsed = runCatching { Direction.valueOf("NORTH") }.getOrNull()
        assertEquals(Direction.NORTH, parsed)

        val invalid = runCatching { Direction.valueOf("UP") }.getOrNull()
        assertNull(invalid)
    }
}
