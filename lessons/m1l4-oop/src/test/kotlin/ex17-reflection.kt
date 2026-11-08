import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Тема 17: KClass и рефлексия.
// RegisterForm и @ValidateNotBlank переиспользуются из темы 16 (ex16-annotation.kt) -
// аннотация сама по себе ничего не делает, здесь мы наконец её читаем через рефлексию.

class Calculator {
    fun add(a: Int, b: Int) = a + b
}

// Простой рантайм-валидатор: находит через рефлексию все свойства,
// помеченные @ValidateNotBlank, и проверяет, что они не пустые.
fun validate(obj: Any): List<String> {
    val kClass = obj::class
    @Suppress("UNCHECKED_CAST")
    val properties = kClass.memberProperties as Collection<KProperty1<Any, *>>

    return properties
        .filter { it.findAnnotation<ValidateNotBlank>() != null }
        .mapNotNull { prop ->
            val value = prop.get(obj)
            if (value is String && value.isBlank()) "${prop.name} must not be blank" else null
        }
}

class ReflectionTest {

    @Test
    fun callMethodByNameViaReflection() {
        val calculator = Calculator()
        val kClass = calculator::class

        // Аналог Java: calculator.getClass().getMethod("add", ...).invoke(calculator, 2, 3)
        val addFunction = kClass.memberFunctions.first { it.name == "add" }
        val result = addFunction.call(calculator, 2, 3)

        assertEquals(5, result)
    }

    @Test
    fun validateFindsAnnotatedBlankProperties() {
        val errors = validate(RegisterForm(login = "", email = "a@b.com"))
        assertEquals(listOf("login must not be blank"), errors)
    }

    @Test
    fun validFormPassesWithoutErrors() {
        val errors = validate(RegisterForm(login = "alice", email = "a@b.com"))
        assertTrue(errors.isEmpty())
    }
}
