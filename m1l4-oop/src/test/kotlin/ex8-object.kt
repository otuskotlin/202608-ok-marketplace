import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

// Тема 8: object и data object.

// object объявляет и создаёт единственный экземпляр класса (singleton) одновременно.
object AppConfig {
    var timeoutMs: Int = 5000
}

// object может реализовывать интерфейсы - удобно для именованных синглтонов-реализаций.
interface IdGenerator {
    fun next(): Long
}

object GlobalIdGenerator : IdGenerator {
    private var last = 0L
    override fun next(): Long = ++last
}

// data object (Kotlin 1.9+) - синглтон с авто-сгенерированным toString,
// удобен как элемент sealed-иерархии наравне с data class (подробнее - тема 11, sealed).
sealed interface CacheState
data object CacheLoading : CacheState
data class CacheLoaded(val items: List<String>) : CacheState

class ObjectTest {

    @Test
    fun objectIsASingleton() {
        AppConfig.timeoutMs = 1000
        // Обращение к object из другого места видит то же самое состояние.
        assertEquals(1000, AppConfig.timeoutMs)
        assertSame(AppConfig, AppConfig)
    }

    @Test
    fun objectCanImplementInterfaces() {
        assertEquals(1L, GlobalIdGenerator.next())
        assertEquals(2L, GlobalIdGenerator.next())
    }

    @Test
    fun dataObjectHasReadableToString() {
        // У data object toString() явно возвращает имя объекта, без этого пришлось бы
        // переопределять toString() вручную, как для обычного object.
        assertEquals("CacheLoading", CacheLoading.toString())
        assertTrue(CacheLoaded(listOf("a")) is CacheState)
    }
}
