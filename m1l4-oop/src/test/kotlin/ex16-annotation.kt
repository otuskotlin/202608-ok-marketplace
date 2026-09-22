import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 16: annotation class.

// Аннотация с параметром и указанием, где именно она может применяться (@Target)
// и до какого момента доживает (@Retention).
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidateNotBlank(val message: String = "must not be blank")

data class RegisterForm(
    @ValidateNotBlank
    val login: String,
    @ValidateNotBlank(message = "email is required")
    val email: String,
    val comment: String = "", // без аннотации - не проверяется
)

class AnnotationTest {

    @Test
    fun annotationIsJustMetadataUntilSomethingReadsIt() {
        // Сама по себе аннотация ничего не делает - это просто метаданные на поле/свойстве.
        // Смысл она приобретает только тогда, когда её кто-то читает: компилятор (как @JvmStatic),
        // фреймворк (как @Autowired) или наш собственный код через рефлексию (см. тему 17).
        val form = RegisterForm(login = "", email = "a@b.com")
        assertEquals("", form.login)
    }
}
