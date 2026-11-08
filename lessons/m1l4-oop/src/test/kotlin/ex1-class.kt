import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// Тема 1: простой класс.

class ThisIsAClass

// Primary constructor может сразу объявлять свойства класса через val/var.
// name - свойство только для чтения (val), age - изменяемое свойство (var).
class Person(
    val name: String,
    var age: Int,
    x: Int = 0, // аргумент конструктора, не свойство
    private val some: Int = 0 // свойство, недоступное снаружи
) {

    lateinit var address: String

    // Вторичный конструктор. Обязан делегировать в primary constructor через this(...),
    // напрямую в теле вторичного конструктора инициализировать свойства нельзя.
    constructor(name: String) : this(name, age = 0)

    // init-блок выполняется сразу после primary constructor.
    // Здесь удобно проверять инварианты класса.
    init {
        address = "Moscow"
        require(age >= 0) { "Age cannot be negative" }
        require(x >= 0) { "X cannot be negative" }
    }

    // Свойство с приватным сеттером: снаружи класса доступно только на чтение,
    // изменить значение можно лишь изнутри класса (например, методом visit()).
    var visits: Int = x
        private set

    // внешний тип один, а приватный - другой
    val phones: List<String>
        field = mutableListOf()

    // Обычный метод класса.
    fun visit() {
        // x // - недоступен
        visits += 1
        println(address)
    }

    // Свойство с явным backing field (field).
    // field - это автоматически сгенерированное поле, к нему можно обратиться
    // только внутри геттера/сеттера этого же свойства.
    var email: String = ""
        set(value) {
            field = value.trim().lowercase()
        }

    private fun q() { //  недоступна снаружи

    }
}

class PersonTest {

    @Test
    fun primaryAndSecondaryConstructor() {
        val alice = Person("Alice", 30)
        assertEquals(30, alice.age)

        // Вторичный конструктор подставляет age = 0 по умолчанию.
        val bob = Person("Bob")
        assertEquals(0, bob.age)
    }

    @Test
    fun initValidatesArguments() {
        // init-блок бросит IllegalArgumentException при отрицательном возрасте.
        assertFailsWith<IllegalArgumentException> {
            Person("Eve", -1)
        }
    }

    @Test
    fun privateSetCanOnlyBeChangedFromInside() {
        val alice = Person("Alice", 30)
        alice.visit()
        alice.visit()
        assertEquals(2, alice.visits)

        // alice.visits = 5 // ERROR: сеттер private, недоступен снаружи класса
    }

    @Test
    fun backingFieldNormalizesValue() {
        val alice = Person("Alice", 30)
        alice.email = "  ALICE@Example.com  "

        // Кастомный сеттер нормализовал значение перед сохранением в field.
        assertEquals("alice@example.com", alice.email)
    }
}
