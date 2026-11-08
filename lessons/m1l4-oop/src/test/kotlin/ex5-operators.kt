import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Тема 5: операторы и их переопределение.

// Операторы можно объявлять как члены класса...
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun unaryMinus() = Point(-x, -y)
    operator fun compareTo(other: Point) =
        (x * x + y * y).compareTo(other.x * other.x + other.y * other.y)
}

// ...а можно как extension-функцию - когда нельзя (или не хочется) менять сам класс.
operator fun Point.times(scale: Int) = Point(x * scale, y * scale)

// --- "Интересные" операторы ---

// get/set: индексированный доступ через квадратные скобки, row[i] / row[i] = value.
class Row(private val cells: MutableList<Int>) {
    operator fun get(index: Int): Int = cells[index]
    operator fun set(index: Int, value: Int) {
        cells[index] = value
    }
}

// invoke: позволяет "вызывать" объект как функцию - counter().
class Counter {
    private var value = 0
    operator fun invoke(): Int = ++value
}

// component1/component2: деструктуризация, val (a, b) = pair.
// У data class они генерируются автоматически, здесь - вручную для обычного класса.
class Pair2(val first: Int, val second: Int) {
    operator fun component1() = first
    operator fun component2() = second
}

class OperatorsTest {

    @Test
    fun arithmeticOperatorsAsMembers() {
        val a = Point(1, 2)
        val b = Point(3, 4)
        assertEquals(Point(4, 6), a + b)
        assertEquals(Point(-1, -2), -a)
        assertTrue(a < b)
    }

    @Test
    fun operatorAsExtensionFunction() {
        val a = Point(1, 2)
        assertEquals(Point(2, 4), a * 2)
    }

    @Test
    fun indexedAccessOperators() {
        val row = Row(mutableListOf(1, 2, 3))
        assertEquals(2, row[1])
        row[1] = 20
        assertEquals(20, row[1])
    }

    @Test
    fun invokeMakesObjectCallable() {
        val counter = Counter()
        assertEquals(1, counter())
        assertEquals(2, counter())
    }

    @Test
    fun componentFunctionsEnableDestructuring() {
        val (a, b) = Pair2(10, 20)
        assertEquals(10, a)
        assertEquals(20, b)
    }
}
