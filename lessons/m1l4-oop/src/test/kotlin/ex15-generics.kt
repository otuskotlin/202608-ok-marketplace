import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 15: дженерики - variance (in/out) и where.
// Базовый generics (generic-функции, ограничения типов) уже был в предыдущем модуле,
// здесь - только вариантность и множественные ограничения.

open class Animal(val name: String)
class Cat(name: String) : Animal(name)

// out T - ковариантность: Producer<Cat> можно использовать там, где ожидается Producer<Animal>.
// T разрешён только в позиции "на выход" (возвращаемые значения), не как параметр методов.
interface Producer<out T> {
    fun produce(): T
}

class CatProducer : Producer<Cat> {
    override fun produce() = Cat("Tom")
}

// in T - контравариантность: Consumer<Animal> можно использовать там, где ожидается Consumer<Cat>.
// T разрешён только в позиции "на вход" (параметры методов), не как возвращаемое значение.
interface Consumer<in T> {
    fun consume(value: T): String
}

class AnimalConsumer : Consumer<Animal> {
    override fun consume(value: Animal) = "consumed ${value.name}"
}

// where - несколько ограничений на один и тот же параметр типа сразу.
fun <T> describeIfComparable(value: T): String where T : CharSequence, T : Comparable<T> =
    "length=${value.length}, value=$value"

class GenericsTest {

    @Test
    fun covarianceAllowsUsingSubtypeProducer() {
        val catProducer: Producer<Cat> = CatProducer()
        // Благодаря out можно присвоить Producer<Cat> переменной типа Producer<Animal>.
        val animalProducer: Producer<Animal> = catProducer
        assertEquals("Tom", animalProducer.produce().name)
    }

    @Test
    fun contravarianceAllowsUsingSupertypeConsumer() {
        val animalConsumer: Consumer<Animal> = AnimalConsumer()
        // Благодаря in можно присвоить Consumer<Animal> переменной типа Consumer<Cat>.
        val catConsumer: Consumer<Cat> = animalConsumer
        assertEquals("consumed Tom", catConsumer.consume(Cat("Tom")))
    }

    @Test
    fun whereCombinesMultipleBounds() {
        assertEquals("length=3, value=abc", describeIfComparable("abc"))
    }
}
