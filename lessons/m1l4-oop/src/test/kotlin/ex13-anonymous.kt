import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Тема 13: анонимные классы (object expression).

interface ClickListener {
    fun onClick(x: Int, y: Int): String
}

// Именованный object - singleton с постоянным состоянием на всё время жизни программы.
object NoopListener : ClickListener {
    override fun onClick(x: Int, y: Int) = "noop"
}

fun handle(listener: ClickListener) = listener.onClick(1, 2)

class AnonymousClassTest {

    @Test
    fun namedObjectIsSingleton() {
        assertEquals("noop", handle(NoopListener))
    }

    @Test
    fun anonymousObjectImplementsInterfaceOnTheFly() {
        // object : ClickListener {...} - анонимная реализация интерфейса "на лету",
        // без объявления отдельного класса, создаётся новый экземпляр при каждом вызове.
        val result = handle(object : ClickListener {
            override fun onClick(x: Int, y: Int) = "clicked at ($x, $y)"
        })
        assertEquals("clicked at (1, 2)", result)
    }

    @Test
    fun anonymousObjectCanCaptureLocalState() {
        var clicks = 0
        val listener = object : ClickListener {
            override fun onClick(x: Int, y: Int): String {
                clicks++
                return "click #$clicks"
            }
        }
        assertEquals("click #1", handle(listener))
        assertEquals("click #2", handle(listener))
        assertTrue(clicks == 2)
    }
}
