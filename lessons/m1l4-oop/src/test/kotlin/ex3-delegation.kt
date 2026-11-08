import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 3: делегирование.

// --- Property delegation ---

// Встроенный делегат by lazy: значение вычисляется один раз,
// при первом обращении к свойству, дальше берётся из кэша.
class Config {
    val heavyValue: String by lazy {
        println("computing heavyValue")
        "computed"
    }
}

// Свой делегат свойства: класс должен реализовать getValue (и setValue для var).
class UpperCaseDelegate {
    private var value: String = ""

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = newValue.uppercase()
    }
}

class User {
    // Каждое обращение к name идёт через getValue/setValue делегата UpperCaseDelegate.
    var name: String by UpperCaseDelegate()
}

// --- Class delegation ---

interface Greetable {
    fun greet(): String
}

class EnglishGreeter : Greetable {
    override fun greet() = "Hello!"
}

// 'by greeter' делегирует ВСЕ методы интерфейса Greetable объекту greeter,
// кроме тех, что переопределены явно (здесь переопределений нет).
class LoggingGreeter(private val greeter: Greetable) : Greetable by greeter

// Без делегирования пришлось бы писать это вручную для каждого метода интерфейса:
class LoggingGreeterManual(private val greeter: Greetable) : Greetable {
    override fun greet(): String = greeter.greet() // ручная делегация, boilerplate
}

class DelegationTest {

    @Test
    fun lazyComputesOnce() {
        val config = Config()
        assertEquals("computed", config.heavyValue)
        assertEquals("computed", config.heavyValue) // второй раз - из кэша, без пересчёта
    }

    @Test
    fun customPropertyDelegateNormalizesValue() {
        val user = User()
        user.name = "alice"
        assertEquals("ALICE", user.name)
    }

    @Test
    fun classDelegationForwardsCallsAutomatically() {
        val greeter = LoggingGreeter(EnglishGreeter())
        assertEquals("Hello!", greeter.greet())
    }
}
