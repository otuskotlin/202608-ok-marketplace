import kotlin.test.Test
import kotlin.test.assertEquals

// Тема 12: inner class.

class Building(val address: String) {

    private val some = "x"

    // Обычный вложенный класс (без inner) НЕ имеет доступа к address -
    // он просто "живёт" в пространстве имён Building, как отдельный класс.
    class Blueprint(val floors: Int) {
        val other = Building("").some // но есть доступ к приватным членам верхнего класса
    }

    // inner class хранит неявную ссылку на внешний экземпляр (Building.this)
    // и поэтому может обращаться к его свойствам и методам.
    inner class Floor(val number: Int) {
        fun describe(): String = "Floor $number of building at $address"
    }
}

class InnerClassTest {

    @Test
    fun nestedClassHasNoAccessToOuterInstance() {
        val blueprint = Building.Blueprint(floors = 5)
        assertEquals(5, blueprint.floors)
    }

    @Test
    fun innerClassCanAccessOuterInstance() {
        val building = Building("Baker Street 221B")
        // inner class создаётся через экземпляр внешнего класса.
        val floor = building.Floor(2)
        assertEquals("Floor 2 of building at Baker Street 221B", floor.describe())
    }
}
