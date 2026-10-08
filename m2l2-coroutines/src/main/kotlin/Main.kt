import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

suspend fun main() {
    delay(5.seconds)
    println("Hello, World!")
}