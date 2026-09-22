import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Тема 2: интерфейс.

// Интерфейс-маркер: не объявляет ни методов, ни свойств.
// Используется просто как "ярлык" типа (проверка через is / when).
interface Marker

// Интерфейс с контрактом без реализации: свойство без backing field,
// реализующий класс обязан его определить.
interface Named {
    val name: String
}

// Интерфейс может содержать метод с реализацией по умолчанию.
// Реализующему классу не обязательно её переопределять.
interface Greeter {
    fun greet(name: String): String = "Hello, $name!"
}

// Класс может реализовывать сколько угодно интерфейсов
// (в отличие от наследования, где базовый класс может быть только один).
class Robot(override val name: String) : Marker, Named, Greeter

// Интерфейсы могут наследовать другие интерфейсы, расширяя контракт.
interface LoudGreeter : Greeter {
    // super.greet(...) вызывает реализацию по умолчанию из родительского интерфейса.
    override fun greet(name: String): String = super.greet(name).uppercase()
}

class LoudRobot(override val name: String) : Named, LoudGreeter

// Реализация Iterable<T> даёт бесплатно forEach/map/filter/toList и другие
// extension-функции стандартной библиотеки - достаточно реализовать iterator().
class Fibonacci(private val count: Int) : Iterable<Long> {
    override fun iterator(): Iterator<Long> = object : Iterator<Long> {
        var a = 0L
        var b = 1L
        var produced = 0

        override fun hasNext() = produced < count

        override fun next(): Long {
            val result = a
            val next = a + b
            a = b
            b = next
            produced++
            return result
        }
    }
}

class InterfaceTest {

    @Test
    fun defaultMethodCanBeOverridden() {
        val robot = Robot("R2D2")
        assertEquals("Hello, R2D2!", robot.greet("R2D2"))

        // LoudGreeter переопределяет greet, используя реализацию по умолчанию из Greeter.
        val loud = LoudRobot("C3PO")
        assertEquals("HELLO, C3PO!", loud.greet("C3PO"))
    }

    @Test
    fun isAs() {
        val robot: Any = Robot("R2D2")
        assertTrue(robot is Named)
        assertTrue(robot !is LoudRobot)
        val named = robot as Named
        val alwaysNull = robot as? LoudRobot
    }

    @Test
    fun implementingIterableGivesStdlibFunctionsForFree() {
        val fib = Fibonacci(6)

        // map/filter/toList - обычные extension-функции над Iterable,
        // писать их вручную для Fibonacci не нужно.
        assertEquals(listOf(0L, 1L, 1L, 2L, 3L, 5L), fib.toList())
        assertEquals(listOf(0L, 2L), fib.filter { it % 2 == 0L })
    }
}
