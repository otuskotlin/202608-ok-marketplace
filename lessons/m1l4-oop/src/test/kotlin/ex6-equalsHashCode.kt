import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Тема 6: equals/hashCode, toString, сравнение ==/!=/===/!==.

// Обычный класс: Kotlin не генерирует equals/hashCode/toString автоматически.
class PlainPoint(val x: Int, val y: Int)

// Класс с ручной реализацией equals/hashCode. Контракт:
// если a.equals(b) == true, то a.hashCode() == b.hashCode() (обратное не обязательно).
class ManualPoint(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ManualPoint) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun toString(): String = "ManualPoint(x=$x, y=$y)"
}

class EqualsHashCodeTest {

    @Test
    fun structuralVsReferentialEquality() {
        val a = PlainPoint(1, 2)
        val b = PlainPoint(1, 2)

        // == вызывает equals(). Без переопределения equals() он сравнивает ссылки,
        // поэтому a и b с одинаковыми полями считаются разными.
        assertFalse(a == b)
        // === сравнивает ссылки напрямую (referential equality); != и !== - отрицания == и ===.
        assertTrue(a !== b)
        assertTrue(a === a)
    }

    @Test
    fun overriddenEqualsGivesStructuralEquality() {
        val a = ManualPoint(1, 2)
        val b = ManualPoint(1, 2)

        // Теперь a == b использует наш equals() - сравнение по значениям полей.
        assertTrue(a == b)
        assertEquals(a.hashCode(), b.hashCode()) // контракт: равные объекты - равные хеши
        assertEquals("ManualPoint(x=1, y=2)", a.toString())
    }
}
