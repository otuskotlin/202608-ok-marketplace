import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Тема 9: companion object.

class Wallet private constructor(val amount: Int, val currency: String) {

    // companion object - контекст внутри класса, доступный без создания экземпляра.
    // Замена static в Java: только один companion object на класс, у него может быть имя (Factory).
    companion object Factory {
        val ZERO = Wallet(0, "USD")

        // "Фабричный метод": конструктор приватный, создавать объекты можно только отсюда.
        fun of(amount: Int, currency: String): Wallet {
            require(amount >= 0) { "Amount must be non-negative" }
            return Wallet(amount, currency)
        }
    }
}

// companion object может реализовывать интерфейс - тогда его можно передавать как значение этого типа.
interface Parser<T> {
    fun parse(raw: String): T
}

class Coord(val x: Int, val y: Int) {
    companion object : Parser<Coord> {
        override fun parse(raw: String): Coord {
            val (x, y) = raw.split(",").map { it.trim().toInt() }
            return Coord(x, y)
        }
    }
}

class CompanionTest {

    @Test
    fun companionActsAsFactory() {
        val wallet = Wallet.of(100, "EUR")
        assertEquals(100, wallet.amount)
        // Обращение по полному имени companion object тоже работает: Wallet.Factory.ZERO
        assertEquals(0, Wallet.ZERO.amount)
    }

    @Test
    fun companionCanImplementInterface() {
        val parser: Parser<Coord> = Coord // companion object передаётся как реализация Parser<Coord>
        val point = parser.parse("3, 4")
        assertEquals(3, point.x)
        assertEquals(4, point.y)
        assertTrue(point is Coord)
    }
}
