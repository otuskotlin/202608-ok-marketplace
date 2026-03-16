import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 4: наследование классов.

// abstract class - нельзя создать экземпляр напрямую. Часть функциональности
// уже реализована (describe), часть оставлена абстрактной (area).
abstract class Shape(val name: String) {

    // Абстрактный метод: у Shape нет реализации, каждый наследник обязан её дать.
    abstract fun area(): Double

    // Обычный метод с реализацией, доступен всем наследникам без переопределения.
    fun describe(): String = "$name with area ${area()}"
}

// open - разрешает дальнейшее наследование от Circle.
// Shape("Circle") - вызов конструктора базового класса.
open class Circle(val radius: Double) : Shape("Circle") {
    override fun area(): Double = Math.PI * radius * radius
}

// Наследник Circle. Circle(radius) - вызов конструктора базового класса.
class ColoredCircle(radius: Double, val color: String) : Circle(radius) {
    override fun area(): Double {
        // super.area() - вызов реализации метода из базового класса (Circle).
        return super.area()
    }

    override fun toString(): String = "$color circle, area=${area()}"
}

class InheritanceTest {

    @Test
    fun abstractClassCannotBeInstantiatedDirectly() {
        // val shape = Shape("x") // ERROR: Cannot create an instance of an abstract class
        val circle = Circle(2.0)
        assertEquals(Math.PI * 4, circle.area())
    }

    @Test
    fun baseClassMethodIsAvailableToDescendants() {
        val circle = Circle(1.0)
        // describe() определён в Shape, но доступен через Circle без переопределения.
        assertEquals("Circle with area ${Math.PI}", circle.describe())
    }

    @Test
    fun childCanCallParentConstructorAndMethod() {
        val colored = ColoredCircle(radius = 2.0, color = "red")
        assertEquals(Math.PI * 4, colored.area())
        assertEquals("red circle, area=${Math.PI * 4}", colored.toString())
    }
}
