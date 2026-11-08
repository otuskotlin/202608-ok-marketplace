import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 11: sealed class / sealed interface.

// sealed - закрытая иерархия: все реализации известны компилятору на этапе компиляции
// (объявлены в этом же файле/модуле). Это даёт exhaustive when без else.
sealed interface LoadState

data object Idle : LoadState
data object Loading : LoadState
data class Success(val items: List<String>) : LoadState
data class Failure(val reason: String) : LoadState
//data class Other(val reason: String) : LoadState

sealed interface LoadState2 {
    data object Idle : LoadState2
    data object Loading : LoadState2
    data class Success(val items: List<String>) : LoadState2
    data class Failure(val reason: String) : LoadState2
}

fun describe(state: LoadState): String = when (state) {
    // Компилятор проверяет, что учтены ВСЕ подтипы - если добавить новый, здесь будет ошибка компиляции.
    is Idle -> "not started"
    is Loading -> "loading..."
    is Success -> "loaded ${state.items.size} items"
    is Failure -> "failed: ${state.reason}"
}

class SealedHierarchyTest {

    @Test
    fun exhaustiveWhenCoversAllSubtypes() {
        assertEquals("not started", describe(Idle))
        assertEquals("loading...", describe(Loading))
        assertEquals("loaded 2 items", describe(Success(listOf("a", "b"))))
        assertEquals("failed: timeout", describe(Failure("timeout")))
    }
}
