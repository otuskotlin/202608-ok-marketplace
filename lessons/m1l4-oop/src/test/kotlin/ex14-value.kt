import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

// Тема 14: value classes (inline classes).

// @JvmInline value class оборачивает одно значение в типобезопасную обёртку.
// На этапе компиляции обёртка в большинстве случаев "стирается" - в рантайме
// это просто String, без накладных расходов на создание отдельного объекта.
@JvmInline
value class SkuId(val id: String) {
    init {
        require(id.isNotBlank()) { "SkuId must not be blank" }
    }
}

@JvmInline
value class OwnerId(val id: String)

data class Listing(val id: SkuId, val ownerId: OwnerId, val title: String)

class ValueClassTest {

    @Test
    fun valueClassPreventsMixingUpDifferentIds() {
        val listing = Listing(
            id = SkuId("p-1"),
            ownerId = OwnerId("u-1"),
            title = "Laptop",
        )

        assertEquals("p-1", listing.id.id)
        // listing.id = listing.ownerId // ERROR: типы SkuId и OwnerId разные,
        // хотя оба внутри хранят просто String (защита от "primitive obsession")
        assertNotEquals(listing.id.id, listing.ownerId.id)
    }

    @Test
    fun initBlockValidatesValue() {
        val error = runCatching { SkuId("") }.exceptionOrNull()
        assertEquals(true, error is IllegalArgumentException)
    }
}
